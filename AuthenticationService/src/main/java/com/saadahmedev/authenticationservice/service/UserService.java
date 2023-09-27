package com.saadahmedev.authenticationservice.service;

import com.saadahmedev.authenticationservice.dto.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<?> createCustomer(UserRequest userRequest);
}
