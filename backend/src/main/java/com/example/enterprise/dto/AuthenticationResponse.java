package com.example.enterprise.dto;

public class AuthenticationResponse {
    private final String token;
    private final String username;
    private final String email;

    public AuthenticationResponse(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}