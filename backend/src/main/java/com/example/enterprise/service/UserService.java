package com.example.enterprise.service;

import com.example.enterprise.dto.UserDeletionDto;
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

    public User updateUser(String username, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userUpdateDto.getUsername().ifPresent(user::setUsername);
        userUpdateDto.getEmail().ifPresent(user::setEmail);
        userUpdateDto.getPassword().ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));

        return userRepository.save(user);
    }


    public void deleteUser(Long userId, UserDeletionDto deletionDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(deletionDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        userRepository.delete(user); // Perform deletion
    }


}
