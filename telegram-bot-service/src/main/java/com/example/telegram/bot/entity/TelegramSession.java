package com.example.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;


@Data
@Builder
@ToString
public class TelegramSession {

    private Long id;

    private String sessionData;

    private boolean isActive;

    private LocalDateTime expirationTime;

    @ToString.Exclude
    @JsonIgnore
    private TelegramUser telegramUser;

}
