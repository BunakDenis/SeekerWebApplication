package com.example.data.models.entity;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TelegramUser {

    private Long id;
    private Long telegramUserId;
    private String username;
    private boolean isActive;
    @ToString.Exclude
    private User user;
    @ToString.Exclude
    private List<TelegramChat> telegramChats;
    @ToString.Exclude
    private TelegramSession telegramSession;

}
