package com.saadahmedev.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaAccountCreationEvent {
    private String accountType;
    private String accountNumber;
    private String email;
    private String subject;
    private long creationTime;
}
