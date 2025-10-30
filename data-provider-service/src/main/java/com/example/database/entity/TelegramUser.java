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
@Table(name = "telegram_users")
@ToString(exclude = {"user", "telegramChats", "telegramSession"})
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_user_id")
    private Long telegramUserId;

    @Column(name = "username")
    private String username;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "telegramUser", fetch = FetchType.EAGER)
    private List<TelegramChat> telegramChats;

    @OneToMany(mappedBy = "telegramUser", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private List<TelegramSession> telegramSessions;

}
