package com.cs.tu.caruserapp.Model;



public class Car {
    private String car_id;
    private String province;
    private String brand;
    private String model;
    private int color;
    private String imageURL;
    private String owner_id;
    private int verify_status;

    public Car(String car_id, String province, String brand, String model, int color, String imageURL, String owner_id, int verify_status) {
        this.car_id = car_id;
        this.province = province;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.imageURL = imageURL;
        this.owner_id = owner_id;
        this.verify_status = verify_status;
    }

    public Car(){

    }


    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public int getVerify_status() {
        return verify_status;
    }

    public void setVerify_status(int verify_status) {
        this.verify_status = verify_status;
    }
}