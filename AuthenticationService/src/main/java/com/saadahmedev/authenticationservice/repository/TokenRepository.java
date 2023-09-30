package com.saadahmedev.authenticationservice.repository;

import com.saadahmedev.authenticationservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
}
