package com.example.fatsewapp.models;
//
//public class User {
//    public String name, email, password;
//
//    public User() {}  // Default constructor required
//
//    public String getName() {
//        return name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public User(String name, String email, String password) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//    }
//}


public class User {
    private String userId;
    private String username;
    private String email;
    private String profileImageUrl;

    // Constructors, getters, setters
    public User() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // Add all getters and setters
}