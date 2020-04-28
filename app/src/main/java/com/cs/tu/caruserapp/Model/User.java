package com.cs.tu.caruserapp.Model;

public class User {

    private String id;
    private String active_carid;
    private String username;
    private String imageURL;
    private String search_name;

    public User(String id, String username, String imageURL, String search_name, String active_carid) {
        this.id = id;
        this.active_carid = active_carid;
        this.username = username;
        this.imageURL = imageURL;
        this.search_name = search_name;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSearch_name() { return search_name; }

    public void setSearch_name(String search_name) { this.search_name = search_name; }

    public String getActive_carid() {
        return active_carid;
    }

    public void setActive_carid(String active_carid) {
        this.active_carid = active_carid;
    }
}
