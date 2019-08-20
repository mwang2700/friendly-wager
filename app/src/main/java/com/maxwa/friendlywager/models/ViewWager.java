package com.maxwa.friendlywager.models;

public class ViewWager {
    public String team1;
    public String team2;
    public String selectedTeam;
    public String sport;
    public long commenceTime;
    public long wagerAmount;
    public double multiplier;

    public ViewWager() {

    }

    public ViewWager(String team1, String team2, String selectedTeam, String sport, long commenceTime, long wagerAmount, double multiplier) {
        this.team1 = team1;
        this.team2 = team2;
        this.selectedTeam = selectedTeam;
        this.sport = sport;
        this.commenceTime = commenceTime;
        this.wagerAmount = wagerAmount;
        this.multiplier = multiplier;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public String getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(String sport) {
        this.selectedTeam = selectedTeam;
    }

    public long getCommenceTime() {
        return commenceTime;
    }

    public double getMultiplier() {
        return multiplier;
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

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public long getWagerAmount() {
        return wagerAmount;
    }

    public void setWagerAmount(long wagerAmount) {
        this.wagerAmount = wagerAmount;
    }
}
