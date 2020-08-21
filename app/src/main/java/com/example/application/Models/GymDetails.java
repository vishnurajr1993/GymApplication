package com.example.application.Models;

import java.io.Serializable;

public class GymDetails implements Serializable {//hhhh
    public String id;
    public String ownerId;
    public String gymName;
    public String gymAddress;
    public String website;
    public String contactPeson;
    public String contactNo;
    public String des;
    public boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public double lattitude;
    public double longitude;
    public GymDetails() {
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getGymName() {
        return gymName;
    }

    public String getGymAddress() {
        return gymAddress;
    }

    public String getWebsite() {
        return website;
    }

    public String getContactPeson() {
        return contactPeson;
    }

    public String getContactNo() {
        return contactNo;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDes() {
        return des;
    }

    public GymDetails(String id, String ownerId, String gymName, String gymAddress, String website, String contactPeson, String contactNo, double lattitude, double longitude, String des) {
        this.id = id;
        this.ownerId = ownerId;
        this.gymName = gymName;
        this.gymAddress = gymAddress;
        this.website = website;
        this.contactPeson = contactPeson;
        this.contactNo = contactNo;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.des = des;
    }
}
