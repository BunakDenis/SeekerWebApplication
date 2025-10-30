package com.example.data.models.entity.dto.telegram;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramUserDTO {

    private Long id;
    private Long telegramUserId;
    private String username;
    private boolean active;
    private LocalDateTime createdAt;

}
