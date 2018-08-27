package com.bmacode17.androideatit.models;

/**
 * Created by User on 14-Aug-18.
 */

public class MyNotification {

    public String body;
    public String title;

    public MyNotification() {
    }

    public MyNotification(String body, String title) {
        this.body = body;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
