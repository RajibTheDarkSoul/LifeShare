package com.example.lifeshare;// com.example.lifeshare.Post.java
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Post {
    private String userId;
    private String postId;
    private String profilepic;
    private String timeAgo;
    private String bloodGroup;
    private int numOfBags;
    private String reqDate;
    private String location;
    private String details;
    private String medicalCertificationImage;
    private List<String> interestedUsers;

    // Constructors, getters, setters...

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getNumOfBags() {
        return numOfBags;
    }

    public void setNumOfBags(int numOfBags) {
        this.numOfBags = numOfBags;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMedicalCertificationImage() {
        return medicalCertificationImage;
    }

    public void setMedicalCertificationImage(String medicalCertificationImage) {
        this.medicalCertificationImage = medicalCertificationImage;
    }

    public List<String> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<String> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }
}
