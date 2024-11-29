package com.example.enterprise.dto;

import jakarta.validation.constraints.NotNull;

public class UserIdDeletionDto {
    @NotNull(message = "User ID is required")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
