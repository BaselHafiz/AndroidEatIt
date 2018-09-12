package com.bmacode17.androideatit.models;

import java.util.List;

/**
 * Created by User on 07-Jul-18.
 */

public class Request {

    private String phone;
    private String name;
    private String address;
    private String total;
    private List<Order> food;
    private String status;
    private String notes;
    private String paymentState;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Order> food , String notes , String paymentState) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.food = food;
        this.status = "0"; // Default is 0 , 0: placed , 1: On my way , 2: Shipped
        this.notes = notes;
        this.paymentState = paymentState;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFood() {
        return food;
    }

    public void setFood(List<Order> food) {
        this.food = food;
    }
}
