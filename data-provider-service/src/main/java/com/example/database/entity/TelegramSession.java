package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "telegram_sessions")
@Cacheable
public class TelegramSession {

    @Id
    private Long id;

    @Column(name = "session_data")
    private String sessionData;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @OneToOne
    @JoinColumn(name = "telegram_user_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private TelegramUser telegramUser;

}
