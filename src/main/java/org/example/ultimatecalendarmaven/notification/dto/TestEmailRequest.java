package org.example.ultimatecalendarmaven.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TestEmailRequest(
        @NotBlank @Email String toEmail
) {}