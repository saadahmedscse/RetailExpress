package com.saadahmedev.authenticationservice.util;

import com.saadahmedev.authenticationservice.dto.ApiResponse;
import com.saadahmedev.authenticationservice.dto.UserRequest;
import com.saadahmedev.authenticationservice.entity.Role;
import com.saadahmedev.authenticationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Component
public class RequestValidator {

    @Autowired
    private UserRepository userRepository;

    @Value("${security.admin.secret-key}")
    private String adminSecret;

    private final SimpleDateFormat dobFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    public ResponseEntity<?> isUserRequestValid(UserRequest userRequest) {
        if (userRequest.getUsername() == null || userRequest.getUsername().isEmpty()) return getError("Username is required");
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) return getError("Username already exist");
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) return getError("Email is required");
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) return getError("Email already exist");
        if (userRequest.getPhone() == null || userRequest.getPhone().isEmpty()) return getError("Phone is required");
        if (userRequest.getFirstName() == null
                || userRequest.getFirstName().isEmpty()
                || userRequest.getLastName() == null
                || userRequest.getLastName().isEmpty()) return getError("Full name is required");
        if (userRequest.getDateOfBirth() == null || userRequest.getDateOfBirth().isEmpty()) return getError("Date of birth is required");
        try {
            dobFormatter.parse(userRequest.getDateOfBirth());
        } catch (ParseException e) {
            return getError("Invalid date of birth format. Required dd-MMM-yyyy");
        }
        if (userRequest.getRole() == null) return getError("Role is required");

        if (userRequest.getRole().equals(Role.ADMIN) || userRequest.getRole().equals(Role.EMPLOYEE)) {
            if (userRequest.getAdminSecret() == null || userRequest.getAdminSecret().isEmpty()) return getError("Required admin secret key");
            if (!userRequest.getAdminSecret().equals(adminSecret)) return getError("Invalid admin secret key");
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) return getError("Password is required");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<?> getError(String message) {
        return ResponseEntity.badRequest().body(new ApiResponse(false, message));
    }
}
