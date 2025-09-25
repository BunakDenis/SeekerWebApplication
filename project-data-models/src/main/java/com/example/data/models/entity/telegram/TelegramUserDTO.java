package com.example.data.models.entity.telegram;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramUserDTO {

    private Long id;
    private Long telegramUserId;
    private String username;
    private boolean isActive;

}
