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
public class TelegramSession {

    @Id
    private Long id;

    @Column(name = "session_data")
    private String sessionData;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @ManyToOne(targetEntity = TelegramUser.class)
    @ToString.Exclude
    private TelegramUser telegramUser;

}
