package com.saadahmedev.accountservice.repository;

import com.saadahmedev.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findAllByUserId(long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByUserIdAndAccountNumber(long userId, String accountNumber);

    void deleteByUserIdAndAccountNumber(long userId, String accountNumber);

//    void deleteAllByUserId(long userId);
}
