package com.example.enterprise.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {
    private String username;
    private String password;

    public LoginDto() {}

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
