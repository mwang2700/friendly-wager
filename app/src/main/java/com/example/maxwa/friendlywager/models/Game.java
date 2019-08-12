package com.example.maxwa.friendlywager.models;

public class Game {
    public String team1;
    public String team2;
    public String sport;
    public long commenceTime;
    public double multiplier1, multiplier2;

    public Game() {

    }

    public Game(String team1, String team2, String sport, long commenceTime, double multiplier1, double multiplier2) {
        this.team1 = team1;
        this.team2 = team2;
        this.sport = sport;
        this.commenceTime = commenceTime;
        this.multiplier1 = multiplier1;
        this.multiplier2 = multiplier2;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public long getCommenceTime() {
        return commenceTime;
    }

    public double getMultiplier1() {
        return multiplier1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public void setCommenceTime(long commenceTime) {
        this.commenceTime = commenceTime;
    }

    public void setMultiplier1(double multiplier1) {
        this.multiplier1 = multiplier1;
    }

    public void setMultiplier2(double multiplier2) {
        this.multiplier2 = multiplier2;
    }

    public double getMultiplier2() {
        return multiplier2;
    }
}
