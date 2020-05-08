package com.cs.tu.caruserapp.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String sender_car_id;
    private String receiver_car_id;
    private String message;
    private boolean isseen;
    private String time;
    private String date;

    public Chat(String sender, String receiver, String sender_car_id, String receiver_car_id, String message, boolean isSeen, String time, String date) {
        this.sender = sender;
        this.receiver = receiver;
        this.sender_car_id = sender_car_id;
        this.receiver_car_id = receiver_car_id;
        this.message = message;
        this.isseen = isseen;
        this.time = time;
        this.date = date;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
