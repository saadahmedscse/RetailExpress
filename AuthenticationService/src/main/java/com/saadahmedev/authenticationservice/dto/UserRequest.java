package com.saadahmedev.authenticationservice.dto;

import com.saadahmedev.authenticationservice.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Role role;
}
