package com.example.data.models.entity;

import lombok.*;

import java.time.LocalDateTime;
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
    private boolean active;
    private LocalDateTime createdAt;
    @ToString.Exclude
    private User user;
    private List<TelegramChat> telegramChats;
    private List<TelegramSession> telegramSessions;

}
