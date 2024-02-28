package com.example.lifeshare;
// InterestedUser.java
public class InterestedUser {
    private String userId;
    private String userName;
    private String profilePicUrl;

    private String phoneNumber;

    // Add constructor and getters/setters
    // You can modify the fields based on your actual data structure


    public InterestedUser(String userId, String userName, String profilePicUrl,String phone) {
        this.userId = userId;
        this.userName = userName;
        this.profilePicUrl = profilePicUrl;
        this.phoneNumber=phone;
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

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

//    public boolean isInterested() {
//        return isInterested;
//    }
//
//    public void setInterested(boolean interested) {
//        isInterested = interested;
//    }
}
