package com.saadahmedev.notificationservice.service;

public interface NotificationService {

    void onUserCreationEvent(String event);
    void onAccountCreationEvent(String event);
}
