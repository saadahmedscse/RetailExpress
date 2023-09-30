package com.saadahmedev.authenticationservice.service;

import com.saadahmedev.authenticationservice.dto.UserRequest;
import com.saadahmedev.authenticationservice.entity.User;
import com.saadahmedev.authenticationservice.repository.UserRepository;
import com.saadahmedev.authenticationservice.util.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> createUser(UserRequest userRequest) {
        ResponseEntity<?> validationResult = requestValidator.isUserRequestValid(userRequest);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        try {
            userRepository.save(mapUser(userRequest));
            return ServerResponse.created("User created successfully");
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
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
