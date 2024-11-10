package com.example.enterprise_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    private Set<String> roles;
}