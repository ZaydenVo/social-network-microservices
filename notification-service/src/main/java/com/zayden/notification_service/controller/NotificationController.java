package com.zayden.notification_service.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.zayden.event.dto.NotificationEvent;
import com.zayden.notification_service.dto.request.Recipient;
import com.zayden.notification_service.dto.request.SendEmailRequest;
import com.zayden.notification_service.service.EmailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    EmailService emailService;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent notificationEvent) {
        emailService.sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(notificationEvent.getRecipient()).build())
                .subject(notificationEvent.getSubject())
                .htmlContent(notificationEvent.getBody())
                .build());
    }
}
