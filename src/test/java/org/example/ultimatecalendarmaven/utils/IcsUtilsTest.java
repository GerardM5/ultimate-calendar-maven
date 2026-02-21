package org.example.ultimatecalendarmaven.utils;

import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IcsUtilsTest {

    @Test
    void buildIcs_containsRequiredICalendarFields() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        OffsetDateTime now = OffsetDateTime.of(2025, 6, 15, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(2025, 6, 15, 11, 0, 0, 0, ZoneOffset.UTC);

        Tenant tenant = Tenant.builder().id(UUID.randomUUID()).name("Test Tenant").slug("test-tenant").build();

        ServiceEntity service = ServiceEntity.builder()
                .id(UUID.randomUUID()).name("Corte de pelo").durationMin(60).tenant(tenant).build();

        Staff staff = Staff.builder()
                .id(UUID.randomUUID()).name("Ana García").tenant(tenant).build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID()).name("Juan Pérez").email("juan@example.com").tenant(tenant).build();

        Appointment appointment = Appointment.builder()
                .id(id)
                .tenant(tenant)
                .service(service)
                .staff(staff)
                .customer(customer)
                .startsAt(now)
                .endsAt(end)
                .createdAt(now)
                .build();

        String ics = IcsUtils.buildIcs(appointment);

        assertThat(ics).contains("BEGIN:VCALENDAR");
        assertThat(ics).contains("VERSION:2.0");
        assertThat(ics).contains("METHOD:REQUEST");
        assertThat(ics).contains("BEGIN:VEVENT");
        assertThat(ics).contains("UID:11111111-1111-1111-1111-111111111111@ultimate-calendar");
        assertThat(ics).contains("DTSTART:20250615T100000Z");
        assertThat(ics).contains("DTEND:20250615T110000Z");
        assertThat(ics).contains("SUMMARY:Corte de pelo");
        assertThat(ics).contains("DESCRIPTION:Cita con Ana García");
        assertThat(ics).contains("END:VEVENT");
        assertThat(ics).contains("END:VCALENDAR");
    }

    @Test
    void buildIcs_escapesSpecialCharacters() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.of(2025, 6, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        Tenant tenant = Tenant.builder().id(UUID.randomUUID()).name("Test Tenant").slug("test-tenant").build();

        ServiceEntity service = ServiceEntity.builder()
                .id(UUID.randomUUID()).name("Service, with; special\\chars").durationMin(30).tenant(tenant).build();

        Staff staff = Staff.builder()
                .id(UUID.randomUUID()).name("Staff").tenant(tenant).build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID()).name("Customer").email("c@example.com").tenant(tenant).build();

        Appointment appointment = Appointment.builder()
                .id(id).tenant(tenant).service(service).staff(staff).customer(customer)
                .startsAt(now).endsAt(now.plusHours(1)).createdAt(now)
                .build();

        String ics = IcsUtils.buildIcs(appointment);

        assertThat(ics).contains("SUMMARY:Service\\, with\\; special\\\\chars");
    }
}
