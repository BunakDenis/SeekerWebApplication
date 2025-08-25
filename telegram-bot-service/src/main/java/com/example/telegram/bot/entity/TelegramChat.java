package com.example.telegram.bot.entity;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TelegramChat {

    private Long id;

    private String uiElement;

    private String uiElementValue;

    private String chatState;

    @ToString.Exclude
    private TelegramUser telegramUser;

}
