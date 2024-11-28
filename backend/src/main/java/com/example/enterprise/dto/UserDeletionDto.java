package com.example.enterprise.dto;

import jakarta.validation.constraints.NotBlank;

public class UserDeletionDto {
    @NotBlank(message = "Password is required for account deletion.")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
