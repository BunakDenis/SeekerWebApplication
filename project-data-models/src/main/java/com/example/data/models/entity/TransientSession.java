package com.example.data.models.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransientSession {

    private Long id;

    private String data;

    private boolean active;

    @ToString.Exclude
    private TelegramSession telegramSession;

}
