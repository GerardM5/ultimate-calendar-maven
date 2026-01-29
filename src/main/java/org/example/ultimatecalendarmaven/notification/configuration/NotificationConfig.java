package org.example.ultimatecalendarmaven.notification.configuration;

import org.example.ultimatecalendarmaven.notification.service.EmailGateway;
import org.example.ultimatecalendarmaven.notification.service.LogEmailGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public EmailGateway emailGateway() {
        return new LogEmailGateway();
    }
}