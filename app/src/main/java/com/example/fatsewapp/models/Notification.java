package com.example.fatsewapp.models;

public class Notification {
    private String fromUserId;
    private String text;
    private String postId;
    private boolean isPost;
    private long timestamp;

    public Notification(String currentUserId, String likedYourPost, String postId, long l) {
        // Required for Firebase
    }

    public Notification(String userId, String text, String postId, long timestamp, boolean isPost) {
        this.fromUserId = userId;
        this.text = text;
        this.postId = postId;
        this.timestamp = timestamp;
        this.isPost = isPost;
    }

    public Notification() {
    }


    // Getters and setters
    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public boolean isPost() { return isPost; }
    public void setPost(boolean post) { isPost = post; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
