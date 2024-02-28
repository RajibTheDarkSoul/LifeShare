package com.example.lifeshare;

public class ChatMessage {
    private long timestamp;
    private String message;
    private  String otherUserId;


    // Required default constructor for Firebase
    public ChatMessage() {
    }

    public ChatMessage(long timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public ChatMessage(long timestamp, String message, String otherUserId) {
        this.timestamp = timestamp;
        this.message = message;
        this.otherUserId = otherUserId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }
}

