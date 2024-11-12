package com.example.enterprise_app.controller;

import com.example.enterprise_app.dto.JwtResponse;
import com.example.enterprise_app.dto.LoginRequest;
import com.example.enterprise_app.dto.SignupRequest;
import com.example.enterprise_app.dto.UserDto;
import com.example.enterprise_app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        UserDto user = authService.registerUser(signUpRequest);
        return ResponseEntity.created(URI.create("/api/users/" + user.getUsername())).body(user);
    }
}