package com.example.enterprise.controller;

import com.example.enterprise.dto.AuthenticationResponse;
import com.example.enterprise.dto.LoginDto;
import com.example.enterprise.dto.UserRegistrationDto;
import com.example.enterprise.model.User;
import com.example.enterprise.security.CustomUserDetails;
import com.example.enterprise.service.JwtService;
import com.example.enterprise.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testRegisterUser_success() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");
        registrationDto.setEmail("test@example.com");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        when(userService.registerUser(any())).thenReturn(mockUser);
        when(jwtService.generateToken("testuser")).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = userController.registerUser(registrationDto);

        assertEquals(200, response.getStatusCodeValue());
        AuthenticationResponse authResponse = (AuthenticationResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("mocked-jwt-token", authResponse.getToken());
        assertEquals("testuser", authResponse.getUsername());
        assertEquals("test@example.com", authResponse.getEmail());
    }

    @Test
    public void testRegisterUser_failure() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");
        registrationDto.setEmail("test@example.com");

        when(userService.registerUser(any()))
                .thenThrow(new RuntimeException("Username already exists"));

        ResponseEntity<?> response = userController.registerUser(registrationDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    public void testLoginUser_success() {
        LoginDto loginDto = new LoginDto("testuser", "password");

        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockUserDetails.getUsername()).thenReturn("testuser");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        when(mockUserDetails.getUser()).thenReturn(mockUser);

        when(jwtService.generateToken("testuser")).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = userController.loginUser(loginDto);

        assertEquals(200, response.getStatusCodeValue());
        AuthenticationResponse authResponse = (AuthenticationResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("mocked-jwt-token", authResponse.getToken());
        assertEquals("testuser", authResponse.getUsername());
        assertEquals("test@example.com", authResponse.getEmail());
    }

    @Test
    public void testLoginUser_failure() {
        LoginDto loginDto = new LoginDto("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<?> response = userController.loginUser(loginDto);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    public void testDeleteUser_success() {
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUser()).thenReturn(new User() {{
            setId(1L);
            setUsername("testuser");
        }});
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        doNothing().when(userService).deleteUserById(1L);

        ResponseEntity<?> response = userController.deleteUser();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User account deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteUser_failure() {
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUser()).thenReturn(new User() {{
            setId(1L);
            setUsername("testuser");
        }});
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        doThrow(new RuntimeException("User not found")).when(userService).deleteUserById(1L);

        ResponseEntity<?> response = userController.deleteUser();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testRegisterUser_invalidInput() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("");
        registrationDto.setPassword("password");
        registrationDto.setEmail("invalid-email-format");

        when(userService.registerUser(any()))
                .thenThrow(new RuntimeException("Invalid input data"));

        ResponseEntity<?> response = userController.registerUser(registrationDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid input data", response.getBody());
    }

    @Test
    public void testLoginUser_invalidInput() {
        LoginDto loginDto = new LoginDto("", ""); // Empty username and password

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<?> response = userController.loginUser(loginDto);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    public void testLoginUser_missingAuthenticationPrincipal() {
        LoginDto loginDto = new LoginDto("testuser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<?> response = userController.loginUser(loginDto);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid username or password", response.getBody());
    }


    @Test
    public void testRegisterUser_userAlreadyExists() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");
        registrationDto.setEmail("test@example.com");

        when(userService.registerUser(any()))
                .thenThrow(new RuntimeException("User already exists"));

        ResponseEntity<?> response = userController.registerUser(registrationDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody());
    }

}
