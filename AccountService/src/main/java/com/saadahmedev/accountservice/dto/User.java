package com.saadahmedev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Date createdAt;
    private Date updatedAt;
}
