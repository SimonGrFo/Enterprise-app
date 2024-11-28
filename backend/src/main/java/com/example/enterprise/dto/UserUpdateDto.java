package com.example.enterprise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Optional;

public class UserUpdateDto {
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private Optional<String> username = Optional.empty();

    @Email(message = "Email should be valid")
    private Optional<String> email = Optional.empty();

    @Size(min = 6, message = "Password must be at least 6 characters")
    private Optional<String> password = Optional.empty();

    private String confirmationToken;

    public Optional<String> getUsername() { return username; }
    public void setUsername(Optional<String> username) { this.username = username; }

    public Optional<String> getEmail() { return email; }
    public void setEmail(Optional<String> email) { this.email = email; }

    public Optional<String> getPassword() { return password; }
    public void setPassword(Optional<String> password) { this.password = password; }

    public String getConfirmationToken() { return confirmationToken; }
    public void setConfirmationToken(String confirmationToken) { this.confirmationToken = confirmationToken; }
}
