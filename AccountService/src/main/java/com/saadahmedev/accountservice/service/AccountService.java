package com.saadahmedev.accountservice.service;

import com.saadahmedev.accountservice.dto.DepositRequest;
import com.saadahmedev.accountservice.dto.OpenAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

    ResponseEntity<?> openAccount(long userId, OpenAccountRequest openAccountRequest);

    ResponseEntity<?> deposit(long userId, DepositRequest depositRequest, String secretKey);

    ResponseEntity<?> getAccounts(long userId);

    ResponseEntity<?> getAccount(String accountId);

    ResponseEntity<?> closeAccount(long userId, String accountId, String secretKey);

    ResponseEntity<?> closeAllAccount(long userId, String secretKey);
}
