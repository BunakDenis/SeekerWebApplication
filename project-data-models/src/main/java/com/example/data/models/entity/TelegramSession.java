package com.example.data.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
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
