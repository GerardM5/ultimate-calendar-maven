package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "payment_tenant_fk"))
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", foreignKey = @ForeignKey(name = "payment_appointment_fk"))
    private Appointment appointment; // nullable

    @Column(nullable = false)
    private String provider;

    @Column(name = "amount_cents", nullable = false)
    private int amountCents;

    @Builder.Default
    @Column(nullable = false)
    private String currency = "EUR";

    @Column(nullable = false)
    private String status;

    @Column(name = "external_ref")
    private String externalRef;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
