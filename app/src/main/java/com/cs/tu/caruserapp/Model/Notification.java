package com.cs.tu.caruserapp.Model;

public class Notification {
    private String imageURL;
    private String message;
    private String time;
    private String date;
    private boolean isseen;

    public Notification(String imageURL, String message, String time, String date, boolean isseen) {
        this.imageURL = imageURL;
        this.message = message;
        this.time = time;
        this.date = date;
        this.isseen = isseen;

    }

    public Notification() {
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
