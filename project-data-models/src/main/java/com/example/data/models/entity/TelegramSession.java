package com.example.data.models.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


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

    @JsonIgnore
    private List<PersistentSession> persistentSessions;

    @JsonIgnore
    private List<TransientSession> transientSessions;

}
