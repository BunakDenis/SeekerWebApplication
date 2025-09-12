package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "telegram_sessions")
@Cacheable
public class TelegramSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telegram_user_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private TelegramUser telegramUser;

    @OneToMany(mappedBy = "telegramSession", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<PersistentSession> persistentSessions;

    @OneToMany(mappedBy = "telegramSession", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<TransientSession> transientSessions;

}
