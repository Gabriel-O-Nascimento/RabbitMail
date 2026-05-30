package com.rabbitmail.entity;

import com.rabbitmail.enums.BatchSendStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "batch_sends")
public class BatchSend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "email_message_id", nullable = false)
    private EmailMessage emailMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BatchSendStatus status;

    @Column(name = "total_recipients", nullable = false)
    private Integer totalRecipients;

    @Column(name = "total_success", nullable = false)
    private Integer totalSuccess;

    @Column(name = "total_error", nullable = false)
    private Integer totalError;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = BatchSendStatus.REQUESTED;
        }
        if (totalRecipients == null) {
            totalRecipients = 0;
        }
        if (totalSuccess == null) {
            totalSuccess = 0;
        }
        if (totalError == null) {
            totalError = 0;
        }
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }
}
