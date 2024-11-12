package com.example.enterprise_app.service;

import com.example.enterprise_app.dto.SignupRequest;
import com.example.enterprise_app.dto.UpdateUserRequest;
import com.example.enterprise_app.dto.UserDto;
import com.example.enterprise_app.model.ERole;
import com.example.enterprise_app.model.Role;
import com.example.enterprise_app.model.User;
import com.example.enterprise_app.repository.RoleRepository;
import com.example.enterprise_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(ERole.ROLE_USER);

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(ERole.ROLE_ADMIN);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("hashedPassword");
        testUser.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        testUser.setActive(true);

        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
    }

    @Test
    void createUser_Success() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@test.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(signupRequest);

        assertNotNull(result, "Created user should not be null");
        assertEquals("testuser", result.getUsername(), "Username should match");
        assertEquals("test@test.com", result.getEmail(), "Email should match");
        assertTrue(result.isActive(), "New user should be active");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setEmail("updated@test.com");
        Set<String> roles = new HashSet<>(Arrays.asList("USER", "ADMIN"));
        updateRequest.setRoles(roles);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser("testuser", updateRequest);

        assertNotNull(result, "Updated user should not be null");
        assertEquals("updated@test.com", result.getEmail(), "Email should be updated");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserStatus_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.toggleUserStatus("testuser");

        verify(userRepository).save(any(User.class));
        assertFalse(testUser.isActive(), "User status should be toggled");
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "Should return one user");
        assertEquals("testuser", result.get(0).getUsername(), "Username should match");
    }
}