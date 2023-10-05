package com.saadahmedev.accountservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saadahmedev.accountservice.dto.*;
import com.saadahmedev.accountservice.entity.Account;
import com.saadahmedev.accountservice.entity.AccountType;
import com.saadahmedev.accountservice.feign.UserService;
import com.saadahmedev.accountservice.repository.AccountRepository;
import com.saadahmedev.accountservice.util.RequestValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseEntity<?> openAccount(long userId, String email, OpenAccountRequest openAccountRequest) {
        if (openAccountRequest == null || openAccountRequest.getAccountType() == null) return ServerResponse.badRequest("Account type is required. Eg.. SAVINGS, CURRENT, FDR, RDR, JOINT");

        List<Account> accountList = accountRepository.findAllByUserId(userId);
        Set<AccountType> accountTypeSet = new HashSet<>();

        accountList.forEach((account -> accountTypeSet.add(account.getAccountType())));
        if (accountTypeSet.contains(openAccountRequest.getAccountType())) return ServerResponse.badRequest("User already has a " + openAccountRequest.getAccountType() + " account");

        Date creationTime = new Date();
        Account account = Account.builder()
                .userId(userId)
                .accountNumber(generateAccountNumber())
                .accountType(openAccountRequest.getAccountType())
                .balance(0.0)
                .createdAt(creationTime)
                .updatedAt(creationTime)
                .build();

        KafkaAccountCreationEvent kafkaAccountCreationEvent = KafkaAccountCreationEvent.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .creationTime(creationTime.getTime())
                .email(email)
                .subject("Retail Express Account Opening")
                .build();

        try {
            accountRepository.save(account);
            kafkaTemplate.send("account-opening-event", new ObjectMapper().writeValueAsString(kafkaAccountCreationEvent));

            return ServerResponse.created("Account has been opened successfully");
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }

    @Override
    public ResponseEntity<?> deposit(long userId, DepositRequest depositRequest, String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) return ServerResponse.badRequest("This action only be performed by an Admin or an Employee");
        List<Account> accountList = accountRepository.findAllByUserId(userId);
        if (accountList.isEmpty()) return ServerResponse.badRequest("User has not opened any account yet");

        Map<AccountType, Account> accountMap = new HashMap<>();
        accountList.forEach((account -> accountMap.put(account.getAccountType(), account)));

        ResponseEntity<?> validationResult = RequestValidator.isDepositRequestValid(depositRequest, accountMap);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        Account account = accountMap.get(depositRequest.getAccountType());
        double previousBalance = account.getBalance();
        account.setBalance(account.getBalance() + depositRequest.getAmount());
        account.setUpdatedAt(new Date());

        try {
            Account updatedAccount = accountRepository.save(account);

            KafkaDepositEvent kafkaDepositEvent = KafkaDepositEvent.builder()
                    .subject("Retail Express Amount Deposit")
                    .email(Objects.requireNonNull(userService.getUser(userId).getBody()).getEmail())
                    .accountNumber(updatedAccount.getAccountNumber())
                    .previousAmount(previousBalance)
                    .depositedAmount(depositRequest.getAmount())
                    .currentAmount(updatedAccount.getBalance())
                    .accountType(updatedAccount.getAccountType().name())
                    .depositTime(updatedAccount.getUpdatedAt().getTime())
                    .build();
            kafkaTemplate.send("amount-deposit-event", new ObjectMapper().writeValueAsString(kafkaDepositEvent));

            return ServerResponse.ok(depositRequest.getAmount() + " BDT has been deposited to " + updatedAccount.getAccountNumber() + " account number at " + updatedAccount.getUpdatedAt());
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
    public ResponseEntity<?> withdraw(long userId, WithdrawRequest withdrawRequest) {
        List<Account> accountList = accountRepository.findAllByUserId(userId);
        if (accountList.isEmpty()) return ServerResponse.badRequest("User has not opened any account yet");

        Map<AccountType, Account> accountMap = new HashMap<>();
        accountList.forEach((account -> accountMap.put(account.getAccountType(), account)));

        ResponseEntity<?> validationResult = RequestValidator.isWithdrawRequestValid(withdrawRequest, accountMap);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        Account account = accountMap.get(withdrawRequest.getAccountType());
        double currentBalance = account.getBalance();

        if (withdrawRequest.getAmount() > currentBalance) return ServerResponse.badRequest("Insufficient balance! Available balance: " + currentBalance);

        account.setBalance(currentBalance - withdrawRequest.getAmount());
        account.setUpdatedAt(new Date());

        try {
            accountRepository.save(account);

            KafkaWithdrawEvent kafkaWithdrawEvent = KafkaWithdrawEvent.builder()
                    .subject("Retails Express Balance Withdraw")
                    .email(Objects.requireNonNull(userService.getUser(userId).getBody()).getEmail())
                    .accountNumber(account.getAccountNumber())
                    .previousAmount(currentBalance)
                    .withdrawAmount(withdrawRequest.getAmount())
                    .currentAmount(account.getBalance())
                    .accountType(account.getAccountType().name())
                    .withdrawTime(account.getUpdatedAt().getTime())
                    .build();

            kafkaTemplate.send("amount-withdraw-event", new ObjectMapper().writeValueAsString(kafkaWithdrawEvent));

            return ServerResponse.ok(withdrawRequest.getAmount() + " BDT withdrawn successfully, current available balance: " + account.getBalance());
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> closeAccount(long userId, String accountId, String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) return ServerResponse.badRequest("This action only be performed by an Admin or an Employee");
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
    public ResponseEntity<?> closeAllAccount(long userId, String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) return ServerResponse.badRequest("This action only be performed by an Admin or an Employee");
        List<Account> accountList = accountRepository.findAllByUserId(userId);
        if (accountList.isEmpty()) return ServerResponse.badRequest("No account found associated to your id");

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
