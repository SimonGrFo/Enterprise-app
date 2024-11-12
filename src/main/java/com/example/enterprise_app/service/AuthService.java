package com.example.enterprise_app.service;

import com.example.enterprise_app.dto.SignupRequest;
import com.example.enterprise_app.dto.UserDto;
import com.example.enterprise_app.model.ERole;
import com.example.enterprise_app.model.Role;
import com.example.enterprise_app.model.User;

import com.example.enterprise_app.dto.SignupRequest;
import com.example.enterprise_app.dto.UserDto;
import com.example.enterprise_app.exception.EmailAlreadyExistsException;
import com.example.enterprise_app.exception.UsernameAlreadyExistsException;
import com.example.enterprise_app.model.ERole;
import com.example.enterprise_app.model.Role;
import com.example.enterprise_app.model.User;
import com.example.enterprise_app.repository.RoleRepository;
import com.example.enterprise_app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public UserDto registerUser(SignupRequest signUpRequest) {
        log.info("Starting user registration process for username: {}", signUpRequest.getUsername());

        try {
            // Check if username exists
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                log.warn("Username {} already exists", signUpRequest.getUsername());
                throw new UsernameAlreadyExistsException(signUpRequest.getUsername());
            }

            // Check if email exists
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                log.warn("Email {} already exists", signUpRequest.getEmail());
                throw new EmailAlreadyExistsException(signUpRequest.getEmail());
            }

            // Create new user
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());

            // Log password encoding
            String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
            log.debug("Password encoded successfully");
            user.setPassword(encodedPassword);

            // Handle roles
            Set<String> strRoles = signUpRequest.getRoles();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null || strRoles.isEmpty()) {
                log.info("No roles specified, assigning default ROLE_USER");
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> {
                            log.error("Default role ROLE_USER not found in database");
                            return new RuntimeException("Error: Role USER is not found.");
                        });
                roles.add(userRole);
            } else {
                log.info("Processing requested roles: {}", strRoles);
                strRoles.forEach(role -> {
                    if ("admin".equals(role)) {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> {
                                    log.error("Role ROLE_ADMIN not found in database");
                                    return new RuntimeException("Error: Role ADMIN is not found.");
                                });
                        roles.add(adminRole);
                        log.info("Added ROLE_ADMIN to user");
                    } else {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> {
                                    log.error("Role ROLE_USER not found in database");
                                    return new RuntimeException("Error: Role USER is not found.");
                                });
                        roles.add(userRole);
                        log.info("Added ROLE_USER to user");
                    }
                });
            }

            user.setRoles(roles);
            user.setActive(true);

            log.info("Attempting to save user to database");
            User savedUser = userRepository.save(user);
            log.info("User saved successfully with ID: {}", savedUser.getId());

            UserDto userDto = convertToDto(savedUser);
            log.info("User registration completed successfully for username: {}", userDto.getUsername());

            return userDto;
        } catch (Exception e) {
            log.error("Error during user registration", e);
            throw e;
        }
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        dto.setActive(user.isActive());
        return dto;
    }

    public String authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String jwt = jwtUtils.generateJwtToken(user);
        return jwt;
    }
}