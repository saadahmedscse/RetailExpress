package com.saadahmedev.accountservice.controller;

import com.saadahmedev.accountservice.dto.DepositRequest;
import com.saadahmedev.accountservice.dto.OpenAccountRequest;
import com.saadahmedev.accountservice.service.AccountService;
import com.saadahmedev.accountservice.util.HeaderType;
import com.saadahmedev.accountservice.util.RequestResolver;
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
        return accountService.openAccount(
                Long.parseLong(RequestResolver.getHeader(request, HeaderType.ID)),
                RequestResolver.getHeader(request, HeaderType.EMAIL),
                openAccountRequest
        );
    }

    @PostMapping("/deposit/{userId}")
    public ResponseEntity<?> deposit(HttpServletRequest request, @PathVariable("userId") long userId, @Nullable @RequestBody DepositRequest depositRequest) {
        return accountService.deposit(
                userId,
                depositRequest,
                RequestResolver.getHeader(request, HeaderType.SECRET_KEY)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAccounts(HttpServletRequest request) {
        return accountService.getAccounts(Long.parseLong(RequestResolver.getHeader(request, HeaderType.ID)));
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
                RequestResolver.getHeader(request, HeaderType.SECRET_KEY)
        );
    }

    @GetMapping("/close-all/{userId}")
    private ResponseEntity<?> closeAllAccount(HttpServletRequest request, @PathVariable("userId") long userId) {
        return accountService.closeAllAccount(
                userId,
                request.getHeader("X-ADMIN-SECRET") == null ? request.getHeader("X-EMPLOYEE-SECRET") : request.getHeader("X-ADMIN-SECRET")
        );
    }
}
