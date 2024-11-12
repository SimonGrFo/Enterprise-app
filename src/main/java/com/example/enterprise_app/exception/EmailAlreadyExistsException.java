package com.example.enterprise_app.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email already in use: " + email);
    }
}
