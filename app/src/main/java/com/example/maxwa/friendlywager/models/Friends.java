package com.example.maxwa.friendlywager.models;

public class Friends {
    public String email;
    public String username;

    public Friends() {

    }

    public Friends(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
