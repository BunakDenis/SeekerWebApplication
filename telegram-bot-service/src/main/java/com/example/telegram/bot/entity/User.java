package com.example.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@Builder
@ToString
public class User {

    private Long id;

    private String email;

    private String role;

    private Boolean isActive;

    @ToString.Exclude
    private List<TelegramUser> telegramUsers;

}
