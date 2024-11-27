package com.example.enterprise.controller;

import com.example.enterprise.dto.UserUpdateDto;
import com.example.enterprise.model.User;
import com.example.enterprise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto, Principal principal) {
        User updatedUser = userService.updateUser(principal.getName(), userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }
}
