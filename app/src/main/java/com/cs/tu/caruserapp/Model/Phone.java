package com.cs.tu.caruserapp.Model;

public class Phone {
    private String phone_number;

    public Phone(String phone_number) {
        this.phone_number = phone_number;

    }

    public Phone(){

    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}