package com.example.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TelegramUser {

    private Long id;

    @ToString.Exclude
    @JsonIgnore
    private User user;

    private boolean isActive;

    private String firstName;

    private String lastName;

    private String username;

    @ToString.Exclude
    @JsonIgnore
    private List<TelegramChat> telegramChats;

    @ToString.Exclude
    @JsonIgnore
    private List<TelegramSession> telegramSessions;

}
