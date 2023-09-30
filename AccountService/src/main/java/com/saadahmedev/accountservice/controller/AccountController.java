package com.saadahmedev.accountservice.controller;

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

    @GetMapping
    public ResponseEntity<?> getAccounts(HttpServletRequest request) {
        return accountService.getAccounts(getUserId(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") String accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/close/{id}")
    public ResponseEntity<?> closeAccount(HttpServletRequest request, @PathVariable("id") String accountId) {
        return accountService.closeAccount(getUserId(request), accountId);
    }

    @GetMapping("/close-all")
    private ResponseEntity<?> closeAllAccount(HttpServletRequest request) {
        return accountService.closeAllAccount(getUserId(request));
    }

    private long getUserId(HttpServletRequest request) {
        return Long.parseLong(request.getHeader("X-USER-ID"));
    }
}
