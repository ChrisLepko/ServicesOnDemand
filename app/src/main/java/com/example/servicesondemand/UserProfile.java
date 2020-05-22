package com.example.servicesondemand;

public class UserProfile {
    public String email;
    public String username;
    public String imageURL;

    public UserProfile(){

    }

    public UserProfile(String email, String username, String imageURL) {
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
