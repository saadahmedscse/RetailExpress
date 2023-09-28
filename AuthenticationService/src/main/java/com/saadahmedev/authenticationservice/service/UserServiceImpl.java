package com.saadahmedev.authenticationservice.service;

import com.saadahmedev.authenticationservice.dto.ApiResponse;
import com.saadahmedev.authenticationservice.dto.LoginRequest;
import com.saadahmedev.authenticationservice.dto.LoginResponse;
import com.saadahmedev.authenticationservice.dto.UserRequest;
import com.saadahmedev.authenticationservice.entity.User;
import com.saadahmedev.authenticationservice.repository.UserRepository;
import com.saadahmedev.authenticationservice.security.JwtUtil;
import com.saadahmedev.authenticationservice.util.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> createUser(UserRequest userRequest) {
        ResponseEntity<?> validationResult = requestValidator.isUserRequestValid(userRequest);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        try {
            userRepository.save(mapUser(userRequest));
            return new ResponseEntity<>(new ApiResponse(true, "Customer created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, e.getLocalizedMessage()));
        }
    }

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        ResponseEntity<?> validationResult = requestValidator.isLoginRequestValid(loginRequest);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        log.info("User has been authenticated");
        return new ResponseEntity<>(new LoginResponse(
                true,
                "Logged in successfully",
                jwtUtil.generateToken(loginRequest.getUsername())), HttpStatus.OK);
    }

    private User mapUser(UserRequest userRequest) {
        Date date = new Date();
        return User.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .role(userRequest.getRole())
                .dateOfBirth(userRequest.getDateOfBirth())
                .createdAt(date)
                .updatedAt(date)
                .build();
    }
}
