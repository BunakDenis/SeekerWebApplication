package com.example.database.service.telegram;

import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepo repo;

    public TelegramUser getUserById(Long id) {
        return repo.getTelegramUserById(id);
    }

    public TelegramUser toEntity(TelegramUserDTO dto) {
        return TelegramUser.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .isActive(dto.isActive())
                .build();
    }

    public TelegramUserDTO toDto(TelegramUser user) {
        return TelegramUserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isActive(user.isActive())
                .build();
    }

}
