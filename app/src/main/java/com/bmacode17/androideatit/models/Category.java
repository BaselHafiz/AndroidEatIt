package com.bmacode17.androideatit.models;

/**
 * Created by User on 29-Jun-18.
 */

public class Category {

    private String Name;
    private String Image;

    public Category() {
    }

    public Category(String Name, String Image) {
        this.Name = Name;
        this.Image = Image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }
}
