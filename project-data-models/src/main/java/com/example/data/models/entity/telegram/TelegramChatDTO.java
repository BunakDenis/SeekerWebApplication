package com.example.data.models.entity.telegram;

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

    private Long telegramChatId;

    private String uiElement;

    private String uiElementValue;

    private String chatState;

}
