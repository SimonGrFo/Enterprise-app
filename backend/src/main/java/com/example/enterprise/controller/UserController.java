package com.example.enterprise.controller;

import com.example.enterprise.dto.UserDeletionDto;
import com.example.enterprise.dto.UserUpdateDto;
import com.example.enterprise.model.User;
import com.example.enterprise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import com.example.enterprise.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Injecting UserRepository

    // Update user details (username/email/password updates)
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto, Principal principal) {
        try {
            User updatedUser = userService.updateUser(principal.getName(), userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody UserDeletionDto deletionDto, Principal principal) {
        try {
            // Get the username from the Principal and fetch the user directly in the controller
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userService.deleteUser(user.getId(), deletionDto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User account deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting account: " + e.getMessage());
        }
    }


}
