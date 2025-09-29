package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transient_sessions")
@Cacheable
public class TransientSession {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "transient_session_data")
        private String data;

        @Column(name = "lastWarnTimestamp")
        private LocalDateTime lastWarnTimestamp;

        @Column(name = "is_active", nullable = false)
        private boolean isActive;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "telegram_session_id", referencedColumnName = "id", nullable = false)
        @ToString.Exclude
        private TelegramSession telegramSession;

}
