package com.example.enterprise.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserIdDeletionDto {
    @NotNull(message = "User ID is required")
    private Long id;

}
