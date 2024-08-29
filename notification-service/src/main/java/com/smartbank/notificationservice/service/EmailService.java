package com.smartbank.notificationservice.service;

import com.smartbank.notificationservice.dto.NotificationRequest;
import com.smartbank.notificationservice.dto.NotificationResponse;

public interface EmailService {

    public NotificationResponse sendEmail(String accountNumber,NotificationRequest notificationRequest) throws Exception;

}