package com.s1mar.covid19tracker.data.models;

import java.util.List;

public class MEmployee {
    private String name;
    private String username;
    private String password;
    private boolean onSite;
    private boolean isAdmin;
    private Integer healthStatus;
    private Integer familyHealthStatus;
    private Integer clientHealthStatus;
    private List<String> placesVisited;
    private String currentLocation;
    private String homeTownAddress;
    private List<MClient> clients;

    public MEmployee() {

    }

    public MEmployee(String name,String username,String password) {
        this.name = name;
        this.username = username;
        this.password = password;
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

    public List<String> getPlacesVisited() {
        return placesVisited;
    }

    public void setPlacesVisited(List<String> placesVisited) {
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

    public List<MClient> getClients() {
        return clients;
    }

    public void setClients(List<MClient> clients) {
        this.clients = clients;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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
}
