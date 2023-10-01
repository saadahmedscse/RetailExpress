package com.saadahmedev.authenticationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saadahmedev.authenticationservice.dto.UserRequest;
import com.saadahmedev.authenticationservice.entity.User;
import com.saadahmedev.authenticationservice.repository.UserRepository;
import com.saadahmedev.authenticationservice.util.RequestValidator;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseEntity<?> createUser(UserRequest userRequest) {
        ResponseEntity<?> validationResult = requestValidator.isUserRequestValid(userRequest);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        try {
            sendKafkaEvent(userRepository.save(mapUser(userRequest)));
            return ServerResponse.created("User created successfully");
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }

    @SneakyThrows
    private void sendKafkaEvent(User user) {
        String kafkaEventMessage = "Your account has been created successfully" +
                "\nId: " + user.getId() +
                "\nUsername: " + user.getUsername() +
                "\nEmail: " + user.getEmail() +
                "\nPhone: " + user.getPhone() +
                "\nDate of birth: " + user.getDateOfBirth() +
                "\nAccount Role: " + user.getRole();

        UserCreationEvent userCreationEvent = UserCreationEvent.builder()
                .subject("Retail Express Account Creation")
                .email(user.getEmail())
                .message(kafkaEventMessage)
                .build();

        kafkaTemplate.send("user-creation-event", new ObjectMapper().writeValueAsString(userCreationEvent));
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class UserCreationEvent {
        private String subject;
        private String email;
        private String message;
    }
}
