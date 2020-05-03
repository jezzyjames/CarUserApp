package com.cs.tu.caruserapp.Model;

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private String phone_number;
    private String verify_status;

    public User(String id, String firstname, String lastname, String phone_number, String verify_status) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone_number = phone_number;
        this.verify_status = verify_status;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getVerify_status() {
        return verify_status;
    }

    public void setVerify_status(String verify_status) {
        this.verify_status = verify_status;
    }
}