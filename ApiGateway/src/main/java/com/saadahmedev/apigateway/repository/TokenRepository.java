package com.saadahmedev.apigateway.repository;

import com.saadahmedev.apigateway.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {}
