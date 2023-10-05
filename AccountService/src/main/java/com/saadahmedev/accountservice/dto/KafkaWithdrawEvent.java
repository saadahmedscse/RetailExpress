package com.saadahmedev.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaWithdrawEvent {
    private String subject;
    private String email;
    private String accountNumber;
    private double previousAmount;
    private double withdrawAmount;
    private double currentAmount;
    private String accountType;
    private long withdrawTime;
}
