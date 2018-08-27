package com.bmacode17.androideatit.models;

/**
 * Created by User on 14-Aug-18.
 */

public class Sender {

    public String to;
    public MyNotification notification;

    public Sender(String to, MyNotification notification) {
        this.to = to;
        this.notification = notification;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public MyNotification getNotification() {
        return notification;
    }

    public void setNotification(MyNotification notification) {
        this.notification = notification;
    }
}
