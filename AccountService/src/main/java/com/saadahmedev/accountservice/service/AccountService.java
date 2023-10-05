package com.saadahmedev.accountservice.service;

import com.saadahmedev.accountservice.dto.DepositRequest;
import com.saadahmedev.accountservice.dto.OpenAccountRequest;
import com.saadahmedev.accountservice.dto.WithdrawRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

    ResponseEntity<?> openAccount(long userId, String email, OpenAccountRequest openAccountRequest);

    ResponseEntity<?> deposit(long userId, DepositRequest depositRequest, String secretKey);

    ResponseEntity<?> getAccounts(long userId);

    ResponseEntity<?> getAccount(String accountId);

    ResponseEntity<?> withdraw(long userId, WithdrawRequest withdrawRequest);

    ResponseEntity<?> closeAccount(long userId, String accountId, String secretKey);

    ResponseEntity<?> closeAllAccount(long userId, String secretKey);
}
