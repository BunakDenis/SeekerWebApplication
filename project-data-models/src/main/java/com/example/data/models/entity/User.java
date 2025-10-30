package com.example.data.models.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userDetails", "telegramUsers"})
public class User {

    private Long id;

    private String username;
    private String password;

    private String email;

    private String role;

    private Boolean active;

    private LocalDateTime registeredAt;

    private UserDetails userDetails;

    private List<TelegramUser> telegramUsers;

}
