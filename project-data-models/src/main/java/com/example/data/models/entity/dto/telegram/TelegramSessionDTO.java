package com.example.data.models.entity.dto.telegram;


import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramSessionDTO {

    private Long id;

    private String sessionData;

    private boolean isActive;

    private LocalDateTime expirationTime;

    private TelegramUserDTO telegramUserDTO;

}