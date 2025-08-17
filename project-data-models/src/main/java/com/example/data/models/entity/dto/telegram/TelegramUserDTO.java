package com.example.data.models.entity.dto.telegram;

import lombok.*;

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

    private TelegramSessionDTO telegramSession;

}
