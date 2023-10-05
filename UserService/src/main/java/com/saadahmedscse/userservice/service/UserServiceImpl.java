package com.saadahmedscse.userservice.service;

import com.saadahmedscse.userservice.entity.User;
import com.saadahmedscse.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> getUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ServerResponse.badRequest("No user found with id " + id);

        return ServerResponse.body(optionalUser.get());
    }
}
