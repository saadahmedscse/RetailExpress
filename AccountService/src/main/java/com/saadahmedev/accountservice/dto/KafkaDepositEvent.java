package com.saadahmedev.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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