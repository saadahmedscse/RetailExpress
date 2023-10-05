package com.saadahmedev.accountservice.dto;

import com.saadahmedev.accountservice.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    private AccountType accountType;
    private Double amount;
}
