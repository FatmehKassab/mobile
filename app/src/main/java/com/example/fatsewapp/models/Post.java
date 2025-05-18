package com.example.fatsewapp.models;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String postId;
    private String projectId;
    private String userId;
    private String title;
    private String description;
    private String imageUrl;
    private long timestamp;
    private long likesCount;
    private long commentsCount;
    private String username;
    private List<String> likes;

    public Post() {
        // Required empty constructor for Firebase
    }

    public Post(String postId, String projectId, String userId, String title, String description) {
        this.postId = postId;
        this.projectId = projectId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.imageUrl = "";
        this.timestamp = System.currentTimeMillis();
        this.likesCount = 0;
        this.commentsCount = 0;
        this.likes = new ArrayList<>();
        this.username = "";
    }
    // Getters and setters
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
