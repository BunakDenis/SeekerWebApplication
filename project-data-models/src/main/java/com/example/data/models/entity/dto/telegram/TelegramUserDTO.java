package com.example.data.models.entity.dto.telegram;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramUserDTO {

    private Long id;

    private boolean isActive;

    private String firstName;

    private String lastName;

    private String username;

    private List<TelegramChatDTO> telegramChats;

    private TelegramSessionDTO telegramSession;

}
