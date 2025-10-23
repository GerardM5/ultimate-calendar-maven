package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "consent",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_consent_customer_purpose_channel",
           columnNames = {"customer_id", "purpose", "channel"}
       ))
public class Consent {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "consent_tenant_fk"))
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "consent_customer_fk"))
    private Customer customer;

    @Column(nullable = false)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channel;

    @Column(nullable = false)
    private boolean granted;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (timestamp == null) timestamp = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
