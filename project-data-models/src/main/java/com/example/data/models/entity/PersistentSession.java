package com.example.data.models.entity;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"telegramSession"})
public class PersistentSession {

    private Long id;

    private String data;

    private boolean active;

    private TelegramSession telegramSession;

}
