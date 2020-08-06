package com.example.application.Models;

public class GymOwnerData {
    String id;
    String fname;
    String lname;
    String userName;
    String password;
    String contactNo;
    int role;//0 for Gymowner 1 for cust
    public GymOwnerData() {
    }

    public String getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getContactNo() {
        return contactNo;
    }

    public GymOwnerData(String id, String fname, String lname, String userName, String password, String contactNo,int role) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.userName = userName;
        this.password = password;
        this.contactNo = contactNo;
        this.role = role;
    }

    public int getRole() {
        return role;
    }
}
