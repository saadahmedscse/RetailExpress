package com.saadahmedev.notificationservice.service;

public interface NotificationService {

    void onUserCreationEvent(String event);

    void onDepositEvent(String event);

    void onAccountCreationEvent(String event);

    void onWithdrawEvent(String event);
}
