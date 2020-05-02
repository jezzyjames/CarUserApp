package com.cs.tu.caruserapp.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Car implements Parcelable {

    private String car_id;
    private String province;
    private String brand;
    private String model;
    private String color;
    private String imageURL;
    private String owner_id;

    public Car(String id, String province, String brand, String model, String color, String imageURL, String owner_id) {
        this.car_id = id;
        this.province = province;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.imageURL = imageURL;
        this.owner_id = owner_id;
    }

    public Car(){

    }

    protected Car(Parcel in) {
        car_id = in.readString();
        province = in.readString();
        brand = in.readString();
        model = in.readString();
        color = in.readString();
        imageURL = in.readString();
        owner_id = in.readString();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

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

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(car_id);
        dest.writeString(province);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(color);
        dest.writeString(imageURL);
        dest.writeString(owner_id);
    }
}