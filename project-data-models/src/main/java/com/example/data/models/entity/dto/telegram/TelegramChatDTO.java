package com.example.data.models.entity.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramChatDTO {

    private Long id;
    private String uiElement;
    private String uiElementValue;
    private String chatState;
    private Long telegramUserId;

}
