package com.example.enterprise_app.service;

import com.example.enterprise_app.dto.*;
import com.example.enterprise_app.exception.*;
import com.example.enterprise_app.model.ERole;
import com.example.enterprise_app.model.Role;
import com.example.enterprise_app.model.User;
import com.example.enterprise_app.repository.RoleRepository;
import com.example.enterprise_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(SignupRequest signupRequest) {
        log.debug("Creating new user with username: {}", signupRequest.getUsername());

        validateNewUser(signupRequest);

        User user = buildUserFromRequest(signupRequest);
        User savedUser = userRepository.save(user);

        log.info("Successfully created user with username: {}", savedUser.getUsername());
        return convertToDto(savedUser);
    }

    private void validateNewUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UsernameAlreadyExistsException(signupRequest.getUsername());
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyExistsException(signupRequest.getEmail());
        }
    }

    private User buildUserFromRequest(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(getRoles(signupRequest.getRoles()));
        user.setActive(true);
        return user;
    }

    private Set<Role> getRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(getDefaultRole());
            return roles;
        }

        strRoles.forEach(roleName -> {
            roles.add("admin".equals(roleName) ? getAdminRole() : getUserRole());
        });

        return roles;
    }

    private Role getDefaultRole() {
        return roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
    }

    private Role getAdminRole() {
        return roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RoleNotFoundException("Admin role not found"));
    }

    private Role getUserRole() {
        return roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("User role not found"));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        userRepository.delete(user);
        log.info("Deleted user with username: {}", username);
    }

    @Transactional
    public UserDto updateUser(String username, UpdateUserRequest updateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        updateUserEmail(user, updateRequest.getEmail());
        updateUserRoles(user, updateRequest.getRoles());

        User savedUser = userRepository.save(user);
        log.info("Updated user with username: {}", username);
        return convertToDto(savedUser);
    }

    private void updateUserEmail(User user, String newEmail) {
        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException(newEmail);
            }
            user.setEmail(newEmail);
        }
    }

    private void updateUserRoles(User user, Set<String> newRoles) {
        if (newRoles != null && !newRoles.isEmpty()) {
            user.setRoles(getRoles(newRoles));
        }
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Changed password for user: {}", username);
    }

    @Transactional
    public void toggleUserStatus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("Toggled status for user: {}. New status: {}", username, user.isActive());
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
}