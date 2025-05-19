package com.example.fatsewapp.models;

import java.io.Serializable;

public class Project implements Serializable {
    private String projectId;
    private String userId;
    private String title;
    private String description;
    private String pattern;
    private String yarnDetails;
    private String imageBase64; // Changed from imageUrl to Base64
    private long createdAt;
    private boolean isPublic;

    // Constructors, getters, setters
    public Project() {}

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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getYarnDetails() {
        return yarnDetails;
    }

    public void setYarnDetails(String yarnDetails) {
        this.yarnDetails = yarnDetails;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    // Add all getters and setters
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    // ... other getters and setters
}
