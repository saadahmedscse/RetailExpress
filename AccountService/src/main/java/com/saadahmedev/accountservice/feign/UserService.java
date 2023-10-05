package com.saadahmedev.accountservice.feign;

import com.saadahmedev.accountservice.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
@Service
public interface UserService {

    @GetMapping("/api/user/{id}")
    ResponseEntity<User> getUser(@PathVariable long id);
}
