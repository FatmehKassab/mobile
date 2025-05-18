package com.example.fatsewapp.models;

public class Notification {
    private String fromUserId;
    private String text;
    private String postId;
    private boolean isPost;
    private long timestamp;

    // Empty constructor for Firebase
    public Notification() {}

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setText(String text) {
        this.text = text;
    }  public String getFromUserId() {
        return fromUserId != null ? fromUserId : "";
    }

    // Constructor for like notifications
    public Notification(String fromUserId, String postId, long timestamp) {
        this.fromUserId = fromUserId;
        this.postId = postId;
        this.timestamp = timestamp;
        this.isPost = true;
        this.text = "liked your post"; // Default text that will be combined with username
    }

    // Getters and setters

    public String getText() { return text; }
    public String getPostId() { return postId; }
    public boolean isPost() { return isPost; }
    public long getTimestamp() { return timestamp; }
    // ... setters if needed ...
}