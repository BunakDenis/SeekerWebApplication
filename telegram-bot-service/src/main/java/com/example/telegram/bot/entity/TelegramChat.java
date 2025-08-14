package com.example.telegram.bot.entity;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramChat {

    private Long id;

    private String uiElement;

    private String uiElementValue;

    private String chatState;

    private TelegramUser telegramUser;

    @Override
    public String toString() {
        return "TelegramChat{" +
                "id=" + id +
                ", uiElement='" + uiElement + '\'' +
                ", chatState='" + chatState + '\'' +
                ", telegramUserId=" + telegramUser.getId() +
                '}';
    }
}
