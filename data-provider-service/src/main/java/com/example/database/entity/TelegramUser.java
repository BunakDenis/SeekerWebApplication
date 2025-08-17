package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "telegram_users")
@ToString
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "telegramUser", fetch = FetchType.EAGER)
    private List<TelegramChat> telegramChats;

    @OneToOne(mappedBy = "telegramUser", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = false)
    @ToString.Exclude
    private TelegramSession telegramSession;

}
