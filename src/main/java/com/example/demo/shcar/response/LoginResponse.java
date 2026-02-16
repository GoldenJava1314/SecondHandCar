package com.example.demo.shcar.response;

public class LoginResponse {

	private String accessToken;
    private boolean isAdmin;
    private String username;

    public LoginResponse(String accessToken, boolean isAdmin, String username) {
        this.accessToken = accessToken;
        this.isAdmin = isAdmin;
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getUsername() {
        return username;
    }
}
