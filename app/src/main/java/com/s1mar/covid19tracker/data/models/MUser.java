package com.s1mar.covid19tracker.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MUser implements Parcelable {
    private String name;
    private String username;
    private String password;
    private boolean onSite;
    private boolean admin;
    private Integer healthStatus;
    private Integer familyHealthStatus;
    private Integer clientHealthStatus;
    private String placesVisited;
    private String currentLocation;
    private String homeTownAddress;
    private boolean client;
    private List<String> clients;

    public MUser() {

    }

    public MUser(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnSite() {
        return onSite;
    }

    public void setOnSite(boolean onSite) {
        this.onSite = onSite;
    }

    public String getPlacesVisited() {
        return placesVisited;
    }

    public void setPlacesVisited(String placesVisited) {
        this.placesVisited = placesVisited;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getHomeTownAddress() {
        return homeTownAddress;
    }

    public void setHomeTownAddress(String homeTownAddress) {
        this.homeTownAddress = homeTownAddress;
    }



    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(Integer healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Integer getFamilyHealthStatus() {
        return familyHealthStatus;
    }

    public void setFamilyHealthStatus(Integer familyHealthStatus) {
        this.familyHealthStatus = familyHealthStatus;
    }

    public Integer getClientHealthStatus() {
        return clientHealthStatus;
    }

    public void setClientHealthStatus(Integer clientHealthStatus) {
        this.clientHealthStatus = clientHealthStatus;
    }

    public boolean isClient() {
        return client;
    }

    public void setIsClient(boolean client) {
        this.client = client;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeByte(this.onSite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.admin ? (byte) 1 : (byte) 0);
        dest.writeValue(this.healthStatus);
        dest.writeValue(this.familyHealthStatus);
        dest.writeValue(this.clientHealthStatus);
        dest.writeString(this.placesVisited);
        dest.writeString(this.currentLocation);
        dest.writeString(this.homeTownAddress);
        dest.writeByte(this.client ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.clients);
    }

    protected MUser(Parcel in) {
        this.name = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.onSite = in.readByte() != 0;
        this.admin = in.readByte() != 0;
        this.healthStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.familyHealthStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientHealthStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.placesVisited = in.readString();
        this.currentLocation = in.readString();
        this.homeTownAddress = in.readString();
        this.client = in.readByte() != 0;
        this.clients = in.createStringArrayList();
    }

    public static final Creator<MUser> CREATOR = new Creator<MUser>() {
        @Override
        public MUser createFromParcel(Parcel source) {
            return new MUser(source);
        }

        @Override
        public MUser[] newArray(int size) {
            return new MUser[size];
        }
    };
}
