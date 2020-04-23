package com.cs.tu.caruserapp.Model;

public class Car {

    private String id;
    private String brand;
    private String model;
    private String color;
    private String imageURL;

    public Car(String id, String brand, String model, String color, String imageURL) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.imageURL = imageURL;
    }

    public Car(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
