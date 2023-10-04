package com.saadahmedev.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaDepositEvent {
    private String subject;
    private String email;
    private String accountNumber;
    private double previousAmount;
    private double depositedAmount;
    private double currentAmount;
    private String accountType;
    private long depositTime;
}