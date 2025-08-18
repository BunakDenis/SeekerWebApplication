package com.example.data.models.entity.dto.telegram;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramUserDTO {

    private Long id;

    private String username;

    private boolean isActive;

    private List<TelegramChatDTO> telegramChats;

    private TelegramSessionDTO telegramSession;

}
