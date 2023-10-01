package com.saadahmedev.notificationservice.service;

import com.google.gson.Gson;
import com.saadahmedev.notificationservice.dto.UserCreationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void onUserCreationEvent(String event) {
        log.info(event);
        UserCreationEvent userCreationEvent = new Gson().fromJson(event, UserCreationEvent.class);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userCreationEvent.getEmail());
        email.setSubject(userCreationEvent.getSubject());
        email.setText(userCreationEvent.getMessage());
        email.setSentDate(new Date());

        javaMailSender.send(email);
    }

    @Override
    public void onAccountCreationEvent(String event) {
        //
    }
}