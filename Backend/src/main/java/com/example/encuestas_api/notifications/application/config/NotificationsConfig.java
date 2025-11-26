package com.example.encuestas_api.notifications.application.config;

import com.example.encuestas_api.notifications.domain.service.NotificationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationsConfig {
    @Bean
    NotificationFactory notificationFactory() { return new NotificationFactory(); }
}
