package com.example.data.models.entity;

import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private Long id;

    private String username;
    private String password;

    private String email;

    private String role;

    private Boolean isActive;

    private UserDetails userDetails;

    private List<TelegramUser> telegramUsers;

}
