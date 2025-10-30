package com.example.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String role;

    @Column(nullable = false)
    private Boolean active;

    @Column
    private LocalDateTime registeredAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserDetails userDetails;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<VerificationCode> verificationCode;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<TelegramUser> telegramUsers;

}
