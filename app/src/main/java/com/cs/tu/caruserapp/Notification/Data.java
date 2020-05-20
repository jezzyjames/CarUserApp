package com.cs.tu.caruserapp.Notification;

public class Data {
    private String sender_id;
    private String sender_car_id;
    private String sender_car_province;
    private int icon;
    private String body;
    private String title;
    private String receiver;
    private String receiver_car_id;
    private String receiver_car_province;


    public Data(String sender_id, String sender_car_id, String sender_car_province, int icon, String body, String title, String receiver, String receiver_car_id, String receiver_car_province) {
        this.sender_id = sender_id;
        this.sender_car_id  = sender_car_id;
        this.sender_car_province = sender_car_province;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.receiver = receiver;
        this.receiver_car_id = receiver_car_id;
        this.receiver_car_province = receiver_car_province;
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

    public String getSender_car_province() {
        return sender_car_province;
    }

    public void setSender_car_province(String sender_car_province) {
        this.sender_car_province = sender_car_province;
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

    public String getReceiver_car_province() {
        return receiver_car_province;
    }

    public void setReceiver_car_province(String receiver_car_province) {
        this.receiver_car_province = receiver_car_province;
    }
}
