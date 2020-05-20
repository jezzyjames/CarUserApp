package com.cs.tu.caruserapp.Model;

public class Chatlist {
    public String id;
    public String sender_car_id;
    public String receiver_car_id;
    public String sender_car_province;
    public String receiver_car_province;

    public Chatlist(String id, String sender_car_id, String receiver_car_id, String sender_car_province, String receiver_car_province){
        this.id = id;
        this.sender_car_id = sender_car_id;
        this.sender_car_province = sender_car_province;
        this.receiver_car_id = receiver_car_id;
        this.receiver_car_province = receiver_car_province;


    }

    public Chatlist(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_car_id() {
        return sender_car_id;
    }

    public void setSender_car_id(String sender_car_id) {
        this.sender_car_id = sender_car_id;
    }

    public String getReceiver_car_id() {
        return receiver_car_id;
    }

    public void setReceiver_car_id(String receiver_car_id) {
        this.receiver_car_id = receiver_car_id;
    }

    public String getSender_car_province() {
        return sender_car_province;
    }

    public void setSender_car_province(String sender_car_province) {
        this.sender_car_province = sender_car_province;
    }

    public String getReceiver_car_province() {
        return receiver_car_province;
    }

    public void setReceiver_car_province(String receiver_car_province) {
        this.receiver_car_province = receiver_car_province;
    }
}
