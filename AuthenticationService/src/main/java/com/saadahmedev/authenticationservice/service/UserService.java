package com.saadahmedev.authenticationservice.service;

import com.saadahmedev.authenticationservice.dto.UserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> createUser(UserRequest userRequest);
}
