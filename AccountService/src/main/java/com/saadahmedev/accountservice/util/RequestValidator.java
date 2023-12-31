package com.saadahmedev.accountservice.util;

import com.saadahmedev.accountservice.dto.DepositRequest;
import com.saadahmedev.accountservice.dto.WithdrawRequest;
import com.saadahmedev.accountservice.entity.Account;
import com.saadahmedev.accountservice.entity.AccountType;
import com.saadahmedev.accountservice.service.ServerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RequestValidator {

    public static ResponseEntity<?> isDepositRequestValid(DepositRequest depositRequest, Map<AccountType, Account> accountMap) {
        if (depositRequest == null) return ServerResponse.badRequest("Deposit request body cannot be null");
        if (depositRequest.getAmount() == null) return ServerResponse.badRequest("Deposit amount is required");
        if (depositRequest.getAmount() < 100) return ServerResponse.badRequest("Minimum deposit amount is 100 BDT");
        if (depositRequest.getAccountType() == null) return ServerResponse.badRequest("Account type is required. Eg.. SAVINGS, CURRENT, FDR, RDR, JOINT");
        if (!accountMap.containsKey(depositRequest.getAccountType())) return ServerResponse.badRequest("Customer has not opened any " + depositRequest.getAccountType() + " yet");

        return ServerResponse.ok();
    }

    public static ResponseEntity<?> isWithdrawRequestValid(WithdrawRequest withdrawRequest, Map<AccountType, Account> accountMap) {
        if (withdrawRequest == null) return ServerResponse.badRequest("Withdraw request body cannot be null");
        if (withdrawRequest.getAmount() == null) return ServerResponse.badRequest("Withdraw amount is required");
        if (withdrawRequest.getAmount() < 50) return ServerResponse.badRequest("Minimum withdraw amount is 50 BDT");
        if (withdrawRequest.getAccountType() == null) return ServerResponse.badRequest("Account type is required. Eg.. SAVINGS, CURRENT, FDR, RDR, JOINT");
        if (!accountMap.containsKey(withdrawRequest.getAccountType())) return ServerResponse.badRequest("Customer has not opened any " + withdrawRequest.getAccountType() + " yet");

        return ServerResponse.ok();
    }
}
