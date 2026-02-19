package org.example.ultimatecalendarmaven.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    /**
     * Only creates the {@link SendGrid} bean when {@code sendgrid.api-key} is
     * non-empty, so the application can start without email configuration.
     */
    @Bean
    @ConditionalOnExpression("'${sendgrid.api-key:}'.length() > 0")
    public SendGrid sendGrid(@Value("${sendgrid.api-key}") String apiKey) {
        return new SendGrid(apiKey);
    }
}
