package br.com.rabbitmessenger.rabbitmessenger.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by saulo on 5/5/16.
 */
public class User extends RealmObject {
    @Required
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
