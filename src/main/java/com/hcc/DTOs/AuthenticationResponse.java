package com.hcc.DTOs;

public class AuthenticationResponse {

//    new AuthenticationResponse(token, userId, loginRequest.getUsername(), userType))

    private String token;
    private Long userId;
    private String username;
    private String userType;


    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String token, Long userId, String username, String userType) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
