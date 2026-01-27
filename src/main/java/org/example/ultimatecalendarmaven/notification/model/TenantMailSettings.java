package org.example.ultimatecalendarmaven.notification.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_mail_settings")
public class TenantMailSettings {

    @Id
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private MailMode mode;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "from_email", nullable = false)
    private String fromEmail;

    @Column(name = "reply_to")
    private String replyTo;

    @Column(name = "provider_identity_id")
    private String providerIdentityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmailStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected TenantMailSettings() {
        // JPA
    }

    public TenantMailSettings(UUID tenantId) {
        this.tenantId = tenantId;
        this.mode = MailMode.SHARED_DOMAIN;
        this.status = EmailStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public MailMode getMode() {
        return mode;
    }

    public void setMode(MailMode mode) {
        this.mode = mode;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getProviderIdentityId() {
        return providerIdentityId;
    }

    public void setProviderIdentityId(String providerIdentityId) {
        this.providerIdentityId = providerIdentityId;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== Helpers =====

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}