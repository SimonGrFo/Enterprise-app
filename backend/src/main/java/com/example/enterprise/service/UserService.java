package com.example.enterprise.service;

import com.example.enterprise.dto.UserUpdateDto;
import com.example.enterprise.model.User;
import com.example.enterprise.repository.UserRepository;
import com.example.enterprise.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto registrationDto) {
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

    public User updateUser(String currentUsername, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userUpdateDto.getUsername() != null
                && !userUpdateDto.getUsername().equals(user.getUsername())
                && userRepository.existsByUsername(userUpdateDto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userUpdateDto.getEmail() != null
                && !userUpdateDto.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(userUpdateDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userUpdateDto.getUsername() != null && !userUpdateDto.getUsername().isEmpty()) {
            user.setUsername(userUpdateDto.getUsername());
        }
        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isEmpty()) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        return userRepository.save(user);
    }

}