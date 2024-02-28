package com.example.lifeshare;

public class SearchUser {
    private String userId;
    private String userName;
    private String bloodType;
    private String address;
    private String lastDonated;
    private String dpImage;

    // Constructor, getters, setters


    public SearchUser(String userId, String userName, String bloodGroup, String address, String lastDonated) {
        this.userId = userId;
        this.userName = userName;
        this.bloodType = bloodGroup;
        this.address = address;
        this.lastDonated = lastDonated;
    }
    public SearchUser() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodGroup) {
        this.bloodType = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastDonated() {
        return lastDonated;
    }

    public void setLastDonated(String lastDonated) {
        this.lastDonated = lastDonated;
    }

    public String getDpImage() {
        return dpImage;
    }

    public void setDpImage(String dpImage) {
        this.dpImage = dpImage;
    }

    public SearchUser(String userId, String userName, String bloodType, String address, String lastDonated, String dpImage) {
        this.userId = userId;
        this.userName = userName;
        this.bloodType = bloodType;
        this.address = address;
        this.lastDonated = lastDonated;
        this.dpImage = dpImage;
    }
}

