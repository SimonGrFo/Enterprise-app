package com.example.enterprise.service;

import com.example.enterprise.dto.UserDeletionDto;
import com.example.enterprise.model.User;
import com.example.enterprise.repository.UserRepository;
import com.example.enterprise.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto registrationDto) {
        // Validate password length before encoding
        if (registrationDto.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());

        return userRepository.save(user);
    }

    public void deleteUser(UserDeletionDto deletionDto) {
        User user = userRepository.findByUsername(deletionDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify the provided password matches the user's stored password
        if (!passwordEncoder.matches(deletionDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Delete the user from the database
        userRepository.delete(user);
    }
}