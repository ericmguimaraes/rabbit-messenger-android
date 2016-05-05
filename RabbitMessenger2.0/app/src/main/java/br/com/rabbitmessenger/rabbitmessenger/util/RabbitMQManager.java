package br.com.rabbitmessenger.rabbitmessenger.util;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.rabbitmessenger.rabbitmessenger.model.Message;
import br.com.rabbitmessenger.rabbitmessenger.model.User;
import br.com.rabbitmessenger.rabbitmessenger.model.UserSingleton;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericmguimaraes on 4/28/2016.
 */
public class RabbitMQManager {

    private static final long SIMULATION_WAIT_TIME = 1000;
    private static RabbitMQManager INSTANCE;

    private static List<OnMessageReceivedListener> onMessageReceivedListenerList;

    ArrayList<String> users;

    private boolean isReceiverStarted = false;

    private RabbitMQManager() {
        users = new ArrayList<>();
        onMessageReceivedListenerList = new ArrayList<>();
    }

    private final Handler simulationHandler = new Handler();

    public static RabbitMQManager getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new RabbitMQManager();
        return INSTANCE;
    }

    public void createOrAcessUser(String user) {
        UserSingleton.getINSTANCE().setUsername(user);


        //TODO
        try {
            // Simulate network access.
            Thread.sleep(SIMULATION_WAIT_TIME);
        } catch (InterruptedException e) {
        }
    }

    public void addOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
        RabbitMQManager.onMessageReceivedListenerList.add(onMessageReceivedListener);
    }

    public void startReceiver() {
        if (onMessageReceivedListenerList.size() == 0)
            throw new RuntimeException("Add at least one OnMessageReceivedListener before using this method.");

        if (!isReceiverStarted) {
            isReceiverStarted = true;


            //TODO
            senderSimulator.run();
        }
    }

    private Runnable senderSimulator = new Runnable() {
        @Override
        public void run() {
            Message message = new Message();
            int userPosition = new Random().nextInt(5);
            if (users != null) {
                while (users.get(userPosition).equals(UserSingleton.getINSTANCE().getUsername())) {
                    userPosition = new Random().nextInt(5);
                }
                message.setContent(users.get(userPosition) + " mandou uma messagem para voce");
                message.setDate(new Date());
                message.setReceiver(UserSingleton.getINSTANCE().getUsername());
                message.setSender(users.get(userPosition));
                message.setRead(false);
                for (OnMessageReceivedListener l : onMessageReceivedListenerList) {
                    l.onMessageReceived(message);
                }
            }
            simulationHandler.postDelayed(senderSimulator, (long) (2000 + new Random().nextFloat()*1000));
        }
    };

    public List<User> getUsers() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<User> query = realm.where(User.class);
        RealmResults<User> users = query.findAll();
        return users.subList(0, users.size());
    }

    public void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public boolean send(Message msg) {
        try {
            // Simulate network access.
            Thread.sleep(SIMULATION_WAIT_TIME);
        } catch (InterruptedException e) {
        }
        //TODO
        return true;
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(Message msg);
    }

}
