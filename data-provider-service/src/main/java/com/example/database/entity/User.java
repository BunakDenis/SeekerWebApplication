package com.example.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private UserDetails userDetails;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<TelegramUser> telegramUsers;

}
