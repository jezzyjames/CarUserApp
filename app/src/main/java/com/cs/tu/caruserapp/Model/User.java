package com.cs.tu.caruserapp.Model;

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private String phone_number;
    private String role;

    public User(String id, String firstname, String lastname, String phone_number, String role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone_number = phone_number;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}