package com.example.database.entity;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telegram_session_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private TelegramSession telegramSession;

}
