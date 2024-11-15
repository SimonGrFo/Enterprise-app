package com.example.enterprise_app.controller;

import com.example.enterprise_app.dto.*;
import com.example.enterprise_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String username) {
        userService.toggleUserStatus(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDto> updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserRequest updateRequest) {
        return ResponseEntity.ok(userService.updateUser(username, updateRequest));
    }


    @PostMapping("/{username}/change-password")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> changePassword(@PathVariable String username, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }
}