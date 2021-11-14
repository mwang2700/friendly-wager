package com.maxwa.friendlywager.models;

public class Member {
    public int img;
    public long points;
    public String username;

    public Member() {

    }

    public Member(int img, String username, long points) {
        this.img = img;
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints (long points) {
        this.points = points;
    }

}
