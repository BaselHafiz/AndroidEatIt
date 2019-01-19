package com.bmacode17.androideatit.models;

/**
 * Created by User on 27-Jun-18.
 */

public class User {

    private String name;
    private String password;
    private String phone;
    private String isStaff;
    private String secureCode;
    private String homeAddress;
    private double balance;

    public User() {
    }

    public User(String name, String password,String secureCode) {
        this.name = name;
        this.password = password;
        this.isStaff = "false";
        this.secureCode = secureCode;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
