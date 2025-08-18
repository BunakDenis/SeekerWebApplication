package com.example.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private Long id;

    private String email;

    private String role;

    private Boolean isActive;

    private UserDetails userDetails;

    private List<TelegramUser> telegramUsers;

}
