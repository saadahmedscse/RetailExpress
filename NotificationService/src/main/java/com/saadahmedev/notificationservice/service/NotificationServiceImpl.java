package com.saadahmedev.notificationservice.service;

import com.google.gson.Gson;
import com.saadahmedev.notificationservice.dto.KafkaAccountCreationEvent;
import com.saadahmedev.notificationservice.dto.KafkaDepositEvent;
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
    public void onDepositEvent(String event) {
        log.info(event);
        KafkaDepositEvent kafkaDepositEvent = new Gson().fromJson(event, KafkaDepositEvent.class);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(kafkaDepositEvent.getEmail());
        email.setSubject(kafkaDepositEvent.getSubject());
        email.setSentDate(new Date(kafkaDepositEvent.getDepositTime()));
        String message = "Account Number: " + kafkaDepositEvent.getAccountNumber() + "\n" +
                "Account Type: " + kafkaDepositEvent.getAccountType() + "\n" +
                "Previous Amount: " + kafkaDepositEvent.getPreviousAmount() + "\n" +
                "Deposited Amount: " + kafkaDepositEvent.getDepositedAmount() + "\n" +
                "Current Amount: " + kafkaDepositEvent.getCurrentAmount() + "\n" +
                "Deposit Date: " + new Date(kafkaDepositEvent.getDepositTime());
        email.setText(message);

        javaMailSender.send(email);
    }

    @Override
    public void onAccountCreationEvent(String event) {
        log.info(event);
        KafkaAccountCreationEvent kafkaAccountCreationEvent = new Gson().fromJson(event, KafkaAccountCreationEvent.class);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(kafkaAccountCreationEvent.getEmail());
        email.setSubject(kafkaAccountCreationEvent.getSubject());
        email.setSentDate(new Date(kafkaAccountCreationEvent.getCreationTime()));
        String message = "Congratulations! A " + kafkaAccountCreationEvent.getAccountType() + " account has been opened.\n" +
                "Your account number is: " + kafkaAccountCreationEvent.getAccountNumber() + "\n" +
                "Account opening date: " + new Date(kafkaAccountCreationEvent.getCreationTime());
        email.setText(message);

        javaMailSender.send(email);
    }
}
