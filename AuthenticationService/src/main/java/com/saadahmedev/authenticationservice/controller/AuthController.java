package com.saadahmedev.authenticationservice.controller;

import com.saadahmedev.authenticationservice.dto.LoginRequest;
import com.saadahmedev.authenticationservice.dto.UserRequest;
import com.saadahmedev.authenticationservice.service.UserService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Nullable @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
