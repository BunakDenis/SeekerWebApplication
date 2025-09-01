package com.example.database.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "persistent_sessions")
@Cacheable
public class PersistentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "persistent_session_data")
    private String data;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "persistent_expiration_time")
    private LocalDateTime expirationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telegram_session_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private TelegramSession telegramSession;

}
