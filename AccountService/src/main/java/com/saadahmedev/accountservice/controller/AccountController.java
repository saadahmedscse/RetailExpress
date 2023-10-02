package com.saadahmedev.accountservice.controller;

import com.saadahmedev.accountservice.dto.DepositRequest;
import com.saadahmedev.accountservice.dto.OpenAccountRequest;
import com.saadahmedev.accountservice.service.AccountService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/open")
    public ResponseEntity<?> openAccount(HttpServletRequest request, @Nullable @RequestBody OpenAccountRequest openAccountRequest) {
        return accountService.openAccount(getUserId(request), openAccountRequest);
    }

    @PostMapping("/deposit/{userId}")
    public ResponseEntity<?> deposit(HttpServletRequest request, @PathVariable("userId") long userId, @Nullable @RequestBody DepositRequest depositRequest) {
        return accountService.deposit(userId, depositRequest, getSecretKey(request));
    }

    @GetMapping
    public ResponseEntity<?> getAccounts(HttpServletRequest request) {
        return accountService.getAccounts(getUserId(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") String accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/close/{userId}/{accountId}")
    public ResponseEntity<?> closeAccount(HttpServletRequest request, @PathVariable("userId") long userId, @PathVariable("accountId") String accountId) {
        return accountService.closeAccount(
                userId,
                accountId,
                getSecretKey(request)
                );
    }

    @GetMapping("/close-all/{userId}")
    private ResponseEntity<?> closeAllAccount(HttpServletRequest request, @PathVariable("userId") long userId) {
        return accountService.closeAllAccount(
                userId,
                request.getHeader("X-ADMIN-SECRET") == null ? request.getHeader("X-EMPLOYEE-SECRET") : request.getHeader("X-ADMIN-SECRET")
        );
    }

    private long getUserId(HttpServletRequest request) {
        return Long.parseLong(request.getHeader("X-USER-ID"));
    }

    private String getSecretKey(HttpServletRequest request) {
        return request.getHeader("X-ADMIN-SECRET") == null ? request.getHeader("X-EMPLOYEE-SECRET") : request.getHeader("X-ADMIN-SECRET");
    }
}
