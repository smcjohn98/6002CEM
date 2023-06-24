package com.smc.crafthk.dto;

import java.io.Serializable;

public class UserMetadata implements Serializable {
    private String userId;
    private String deviceToken;
    private String email;
    public UserMetadata() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}