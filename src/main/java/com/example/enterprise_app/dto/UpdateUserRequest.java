package com.example.enterprise_app.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {
    @Email
    private String email;
    private Set<String> roles;
}