package com.saadahmedev.authenticationservice.service;

import com.saadahmedev.authenticationservice.dto.LoginRequest;
import com.saadahmedev.authenticationservice.entity.Token;
import com.saadahmedev.authenticationservice.repository.TokenRepository;
import com.saadahmedev.authenticationservice.security.JwtUtil;
import com.saadahmedev.authenticationservice.util.RequestValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        ResponseEntity<?> validationResult = requestValidator.isLoginRequestValid(loginRequest);
        if (validationResult.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) return validationResult;

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        log.info("User has been authenticated");
        String token = jwtUtil.generateToken(loginRequest.getUsername());

        try {
            tokenRepository.save(new Token(token));
            log.info("Token saved to database");
            return ServerResponse.login.ok("Logged in successfully", token);
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) return ServerResponse.unauthorized("Bearer token is required");

        if (!authorization.startsWith("Bearer ") && authorization.split(" ")[1] == null || authorization.split(" ")[1].isEmpty()) return ServerResponse.unauthorized("Bearer token is required");
        String token = authorization.substring(7);

        try {
            if (!jwtUtil.isTokenValid(token)) return ServerResponse.unauthorized("Token has been expired");
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                tokenRepository.deleteById(token);
                log.info("Token has been deleted from database because of expiration exception");
                return ServerResponse.unauthorized("Token has been expired");
            }
            if (e instanceof MalformedJwtException) {
                return ServerResponse.unauthorized("Invalid JWT Token");
            }
            else return ServerResponse.unauthorized(e.getLocalizedMessage());
        }

        try {
            tokenRepository.deleteById(token);
            log.info("Token has been deleted from database");
            return ServerResponse.ok("Logged out successfully");
        } catch (Exception e) {
            return ServerResponse.internalServerError(e);
        }
    }
}
