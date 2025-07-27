package org.example.cleanprj.models;

public abstract class Person {
    protected User user;

    public Person(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public abstract String getRole();
}
