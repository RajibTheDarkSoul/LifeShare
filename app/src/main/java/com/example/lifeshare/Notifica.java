package com.example.lifeshare;

public class Notifica {
    private String senderUserId;
    private String postId;
    private long timestamp;

    public Notifica() {
        // Default constructor required for Firebase
    }

    public Notifica(String senderUserId, String postId, long timestamp) {
        this.senderUserId = senderUserId;
        this.postId = postId;
        this.timestamp = timestamp;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
