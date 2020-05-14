package com.example.servicesondemand;

public class UserProfile {
    public String email;
    public String username;

    public UserProfile(){

    }

    public UserProfile(String email, String username) {
        this.username = username;
        this.email = email;
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
}
