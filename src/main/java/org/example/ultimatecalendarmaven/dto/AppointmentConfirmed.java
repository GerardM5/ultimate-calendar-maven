package org.example.ultimatecalendarmaven.dto;

public record AppointmentConfirmed(
        String customerName,
        String serviceName,
        String staffName,
        String date,
        String time
) {

}
