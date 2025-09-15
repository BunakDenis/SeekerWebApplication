package com.example.data.models.entity;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TelegramChat {

    private Long id;
    private Long telegramChatId;
    private String uiElement;
    private String uiElementValue;
    private String chatState;
    @ToString.Exclude
    private TelegramUser telegramUser;

}
