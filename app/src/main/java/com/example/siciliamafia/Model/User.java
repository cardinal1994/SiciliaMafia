package com.example.siciliamafia.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String search;
    private String city;
    private int balance;
    private String role;
    private String phoneNumber;

    public User(String id, String username, String imageURL, String search, String city, int balance, String role, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.search = search;
        this.city = city;
        this.balance = balance;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
