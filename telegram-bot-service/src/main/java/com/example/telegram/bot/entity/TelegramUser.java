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

    private String username;

    private boolean isActive;

    @ToString.Exclude
    private User user;

    @ToString.Exclude
    private List<TelegramChat> telegramChats;

    @ToString.Exclude
    private TelegramSession telegramSession;

}
