package com.example.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "telegram_chats")
public class TelegramChat {

    @Id
    private Long id;

    @Column(name = "ui_element")
    private String uiElement;

    @Column(name = "ui_element_value")
    private String uiElementValue;

    @Column(name = "chat_state")
    private String chatState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telegram_user_id", nullable = false)
    @JsonIgnore
    private TelegramUser telegramUser;

    @Override
    public String toString() {
        return "TelegramChat{" +
                "id=" + id +
                ", uiElement='" + uiElement + '\'' +
                ", uiElementValue='" + uiElementValue + '\'' +
                ", chatState='" + chatState + '\'' +
                ", telegramUserId=" + telegramUser.getId() +
                '}';
    }
}
