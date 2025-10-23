package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "appointment",
       indexes = {
           @Index(name = "idx_appt_tenant_time", columnList = "tenant_id, starts_at"),
           @Index(name = "idx_appt_customer_time", columnList = "customer_id, starts_at")
       })
public class Appointment {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "appointment_tenant_fk"))
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, foreignKey = @ForeignKey(name = "appointment_service_fk"))
    private ServiceEntity service;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "appointment_staff_fk"))
    private Staff staff;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "appointment_customer_fk"))
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Builder.Default
    @Column(name = "price_cents", nullable = false)
    private int priceCents = 0;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // Generated column in DB (read-only)
    @Column(name = "active", insertable = false, updatable = false)
    private Boolean active;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (status == null) status = AppointmentStatus.PENDING;
        if (createdAt == null) createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
