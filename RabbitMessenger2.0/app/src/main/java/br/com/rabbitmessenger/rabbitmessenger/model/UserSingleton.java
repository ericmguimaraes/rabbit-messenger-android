package br.com.rabbitmessenger.rabbitmessenger.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericmguimaraes on 4/30/2016.
 */
public class UserSingleton {

    private static UserSingleton INSTANCE;

    private UserSingleton(){
        messages = new ArrayList<>();
    }

    public static UserSingleton getINSTANCE(){
        if(INSTANCE==null)
            INSTANCE = new UserSingleton();
        return INSTANCE;
    }

    private String username;

    private List<Message> messages;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<User> getSenders(){
        List<User> senders = new ArrayList<>();
        outerloop:
        for (Message m:messages) {
            if(!m.isRead()){
                for (User sender: senders) {
                    if(sender.getName().equals(m.getSender()))
                        continue outerloop;
                }
                senders.add(new User(m.getSender()));
            }
        }
        return senders;
    }

    public List<Message> findMessagesBySender(String sender){
        List<Message> response = new ArrayList<>();
        for (Message m:messages) {
            if(m.getSender().equals(sender))
                response.add(m);
        }
        return response;
    }

    public void read(Message message){
        messages.remove(message);
    }

}
