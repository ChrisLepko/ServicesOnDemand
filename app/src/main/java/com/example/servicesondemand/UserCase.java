package com.example.servicesondemand;

public class UserCase {
    public String category;
    public String details;
    public String phoneNumber;
    public String address;

    public UserCase(){

    }

    public UserCase(String category, String details, String phoneNumber, String address) {
        this.category = category;
        this.details = details;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
