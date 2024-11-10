package com.example.enterprise_app.service;

import com.example.enterprise_app.dto.SignupRequest;
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

public class UserServiceTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRole = new Role();
        userRole.setName(ERole.ROLE_USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("hashedPassword");
        testUser.setRoles(Set.of(userRole));
        testUser.setActive(true);

        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER))
                .thenReturn(Optional.of(userRole));
    }

    @Test
    void createUser_Success() {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
        assertTrue(result.isActive());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains("ROLE_USER"));
    }

    @Test
    void createUser_DuplicateUsername() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("new@test.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(request));
    }

    @Test
    void getAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void getUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserByUsername("nonexistent"));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.deleteUser("testuser"));

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void toggleUserStatus_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.toggleUserStatus("testuser");

        assertFalse(testUser.isActive());
    }
}