package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.telegram.bot.entity.TelegramUser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import lombok.*;
import org.modelmapper.ModelMapper;

@Service
@Data
@RequiredArgsConstructor
@Builder
public class TelegramUserService {

    private final ModelMapper modelMapper;

    public TelegramUser toEntity(TelegramUserDTO dto) {
        return modelMapper.map(dto, TelegramUser.class);
    }

    public TelegramUser apiTelegramUserToEntity(User user) {
        return TelegramUser.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(true)
                .build();
    }

    public TelegramUserDTO toDto(TelegramUser user) {
        return modelMapper.map(user, TelegramUserDTO.class);
    }

    public TelegramUserDTO toDto(User user) {
        return null;
    }

}
