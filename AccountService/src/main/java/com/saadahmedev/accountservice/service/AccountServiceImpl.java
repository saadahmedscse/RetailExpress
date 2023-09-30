package com.saadahmedev.accountservice.service;

import com.saadahmedev.accountservice.dto.OpenAccountRequest;
import com.saadahmedev.accountservice.entity.Account;
import com.saadahmedev.accountservice.entity.AccountType;
import com.saadahmedev.accountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ResponseEntity<?> openAccount(long userId, OpenAccountRequest openAccountRequest) {
        if (openAccountRequest == null || openAccountRequest.getAccountType() == null) return ServerResponse.badRequest("Account type is required. Eg.. SAVINGS, CURRENT, FDR, RDR, JOINT");

        List<Account> accountList = accountRepository.findAllByUserId(userId);
        Set<AccountType> accountTypeSet = new HashSet<>();

        accountList.forEach((account -> accountTypeSet.add(account.getAccountType())));
        if (accountTypeSet.contains(openAccountRequest.getAccountType())) return ServerResponse.badRequest("User already has a " + openAccountRequest.getAccountType() + " account");

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(generateAccountNumber())
                .accountType(openAccountRequest.getAccountType())
                .balance(0.0)
                .createdAt(new Date())
                .build();

        try {
            accountRepository.save(account);
            return ServerResponse.created("Account has been opened successfully");
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }

    @Override
    public ResponseEntity<?> getAccounts(long userId) {
        return ServerResponse.body(accountRepository.findAllByUserId(userId));
    }

    @Override
    public ResponseEntity<?> getAccount(String accountId) {
        if (accountId == null || accountId.isEmpty()) return ServerResponse.badRequest("Account number is required");
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountId);

        if (optionalAccount.isPresent()) return ServerResponse.body(optionalAccount);
        else return ServerResponse.badRequest("Account not found");
    }

    @Override
    @Transactional
    public ResponseEntity<?> closeAccount(long userId, String accountId) {
        if (accountId == null || accountId.isEmpty()) return ServerResponse.badRequest("Account number is required");
        Optional<Account> optionalAccount = accountRepository.findByUserIdAndAccountNumber(userId, accountId);

        if (optionalAccount.isPresent()) {
            accountRepository.deleteByUserIdAndAccountNumber(userId, accountId);
            return ServerResponse.ok("Account has been closed");
        }
        else return ServerResponse.badRequest("Account not found");
    }

    @Override
    @Transactional
    public ResponseEntity<?> closeAllAccount(long userId) {
        List<Account> accountList = accountRepository.findAllByUserId(userId);

        if (accountList.isEmpty()) return ServerResponse.badRequest("No account is associated to your id");

        try {
            accountRepository.deleteAll(accountList);
            return ServerResponse.ok("All accounts associated to you has been closed");
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }

    private String generateAccountNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 17; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }
}
