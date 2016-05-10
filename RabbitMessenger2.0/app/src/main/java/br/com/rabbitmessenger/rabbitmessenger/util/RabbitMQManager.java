package br.com.rabbitmessenger.rabbitmessenger.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import br.com.rabbitmessenger.rabbitmessenger.model.Message;
import br.com.rabbitmessenger.rabbitmessenger.model.User;
import br.com.rabbitmessenger.rabbitmessenger.model.UserSingleton;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericmguimaraes on 4/28/2016.
 */
public class RabbitMQManager {

    private static final String SERVER_URL = "ajalvesneto.koding.io";
    private static RabbitMQManager INSTANCE;
    private String EXCHANGE_NAME = "amq.direct";

    private static List<OnMessageReceivedListener> onMessageReceivedListenerList;

    ArrayList<User> users;

    private boolean isReceiverStarted = false;

    RealmConfiguration realmConfig;

    Realm realm;

    Context context;

    private BlockingDeque queue;

    Thread subscribeThread;
    Thread publishThread;

    public void send(Message message) {
        try {
            Log.d("","[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ConnectionFactory factory;

    private void setupConnectionFactory() {
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost(SERVER_URL);
    }

    private RabbitMQManager() {
        users = new ArrayList<>();
        onMessageReceivedListenerList = new ArrayList<>();
        queue = new LinkedBlockingDeque();
        factory = new ConnectionFactory();
        setupConnectionFactory();
    }

    public static RabbitMQManager getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new RabbitMQManager();
        return INSTANCE;
    }

    public void createOrAcessUser(String user){
        UserSingleton.getINSTANCE().setUsername(user);
        if(!isUserCreated(user))
            saveUser(new User(user));
    }

    public void addOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
        RabbitMQManager.onMessageReceivedListenerList.add(onMessageReceivedListener);
    }

    public void startService() throws IOException {
        if (onMessageReceivedListenerList.size() == 0)
            throw new RuntimeException("Add at least one OnMessageReceivedListener before using this method.");

        if (!isReceiverStarted) {
            isReceiverStarted = true;

            subscribe();
            publishToAMQP();
        }
    }


    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Log.i("publishThread","publishThread");
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            Message message = (Message) queue.takeFirst();
                            try{
                                ch.queueDeclare(message.getReceiver(), true, false, false, null);
                                Gson gson = new Gson();
                                ch.basicPublish("", message.getReceiver(), null, gson.toJson(message).getBytes());
                                Log.d("", "[s] " + message);
                                //ch.waitForConfirmsOrDie();
                                ch.close();
                            } catch (Exception e){
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        Log.d("", "Entrou 1");
                        break;
                    } catch (Exception e) {
                        Log.d("", "Entrou 2");
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Log.d("", "Entrou 3");
                            Thread.sleep(200); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.setName("publishThread");
        publishThread.setPriority(Thread.MAX_PRIORITY);
        publishThread.start();
    }


    private class send2 extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... Message) {
            try {

                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                channel.confirmSelect();

                Message message;
                while (true) {
                    message = (Message) queue.takeFirst();

                    channel.queueDeclare(message.getReceiver(), false, false, false, null);
                    String tempstr = "";
                    for (int i = 0; i < Message.length; i++)
                        tempstr += Message[i];

                    channel.basicPublish(EXCHANGE_NAME, message.getReceiver(), null,
                            tempstr.getBytes());

                    channel.close();

                    connection.close();
                }

            } catch (Exception e) {
                // TODO: handle exception
                Log.d("", "Entrou 4");
                e.printStackTrace();
            }
            // TODO Auto-generated method stub
            return null;
        }

    }



    void subscribe()
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {

            while(true) {
                try {
                    Log.i("subscribeThread","subscribeThread");
                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    AMQP.Queue.DeclareOk q = channel.queueDeclare(UserSingleton.getINSTANCE().getUsername(), true, false, false, null);
                    channel.queueBind(q.getQueue(), "amq.direct", UserSingleton.getINSTANCE().getUsername());
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume(UserSingleton.getINSTANCE().getUsername(), true, consumer);
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        Gson gson = new Gson();
                        Message message = gson.fromJson(new String(delivery.getBody()), Message.class);
                        for (OnMessageReceivedListener l : onMessageReceivedListenerList) {
                            l.onMessageReceived(message);
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e1) {
                    Log.e("", "Connection broken: " + e1.getClass().getName());
                    try {
                        Thread.sleep(200); //sleep and then try again
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
        });
        subscribeThread.setName("subscribeThread");
        subscribeThread.setPriority(Thread.MAX_PRIORITY);
        subscribeThread.start();
    }

    public List<User> getUsers() {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        RealmQuery<User> query = realm.where(User.class);
        RealmResults<User> users = query.findAll();
        return users.subList(0, users.size());
    }

    public void saveUser(User user) {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(Message msg);
    }

    public void setContext(Context context) {
        destroy();
        reset();
        INSTANCE.context = context;
        INSTANCE.realmConfig = new RealmConfiguration.Builder(context).build();
    }

    public boolean isUserCreated(String user){
        realm = Realm.getInstance(realmConfig);
        RealmResults<User> r = realm.where(User.class)
                .equalTo("name", user)
                .findAll();
        return r.size()>0;
    }

    public void reset(){
        INSTANCE = new RabbitMQManager();
    }

    public void destroy(){
        if(publishThread!=null)
            publishThread.interrupt();
        if(subscribeThread!=null)
            subscribeThread.interrupt();
    }

}
