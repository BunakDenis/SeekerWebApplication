package com.example.data.models.entity;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PersistentSession {

    private Long id;

    private String data;

    private boolean isActive;

    @ToString.Exclude
    private TelegramSession telegramSession;

}
