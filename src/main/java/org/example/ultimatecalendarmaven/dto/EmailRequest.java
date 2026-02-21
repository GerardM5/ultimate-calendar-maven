package org.example.ultimatecalendarmaven.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.model.Tenant;

@Getter
@Builder
public class EmailRequest {

    private final Tenant tenant;
    private final String to;
    private final String subject;
    private final String htmlContent;

    @Builder.Default
    private final String template = "custom";

    private final Appointment relatedAppointment;

    /** Raw iCalendar (.ics) content to attach to the email, or {@code null} if no attachment. */
    private final String icsContent;
}
