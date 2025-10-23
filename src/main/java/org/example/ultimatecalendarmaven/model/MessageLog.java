package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "message_log",
       indexes = @Index(name = "idx_msg_tenant_created", columnList = "tenant_id, created_at"))
public class MessageLog {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "msg_tenant_fk"))
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channel;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    private String template;

    @Column(columnDefinition = "jsonb")
    private String payload; // raw JSON

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_appointment", foreignKey = @ForeignKey(name = "msg_appt_fk"))
    private Appointment relatedAppointment; // nullable

    @Column(nullable = false)
    private String status; // sent, delivered, failed...

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
