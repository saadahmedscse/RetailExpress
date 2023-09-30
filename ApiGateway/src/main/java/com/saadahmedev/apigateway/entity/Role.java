package com.saadahmedev.apigateway.entity;

import lombok.Getter;

@Getter
public enum Role {
    CUSTOMER("CUSTOMER"), EMPLOYEE("EMPLOYEE"), ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }
}
