package com.chatt.demo;

import com.chatt.demo.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericmguimaraes on 4/28/2016.
 */
public class RabbitMQManager {

    RabbitMessengerInterface rabbitMessengerInterface;

    public RabbitMQManager(RabbitMessengerInterface rabbitMessengerInterface){
        this.rabbitMessengerInterface = rabbitMessengerInterface;
        initReceiver();
    }

    private void initReceiver() {

    }

    public List<String> getUsers(){
        ArrayList users = new ArrayList<String>();
        users.add("eric");
        users.add("tonny");
        users.add("railan  ");
        users.add("eric");
        return users;
    }

    public boolean send(Message msg){
        return true;
    }



    interface RabbitMessengerInterface {
        void onMessageReceived(Message msg);
    }

}
