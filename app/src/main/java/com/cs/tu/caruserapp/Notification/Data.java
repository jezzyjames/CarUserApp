package com.cs.tu.caruserapp.Notification;

public class Data {
    private String sender_id;
    private String sender_car_id;
    private int icon;
    private String body;
    private String title;
    private String receiver;
    private String receiver_car_id;


    public Data(String sender_id, String sender_car_id, int icon, String body, String title, String receiver, String receiver_car_id) {
        this.sender_id = sender_id;
        this.sender_car_id  = sender_car_id;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.receiver = receiver;
        this.receiver_car_id = receiver_car_id;
    }

    public Data() {
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_car_id() {
        return sender_car_id;
    }

    public void setSender_car_id(String sender_car_id) {
        this.sender_car_id = sender_car_id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver_car_id() {
        return receiver_car_id;
    }

    public void setReceiver_car_id(String receiver_car_id) {
        this.receiver_car_id = receiver_car_id;
    }
}
