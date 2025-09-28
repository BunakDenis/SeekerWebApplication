package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class ModelMapperService {

    private final ModelMapper mapper;

    public <T, K> T toEntity (K dto, Class<T> entity) {
        return mapper.map(dto, entity);
    }

    public <T, K> K toDTO (T entity, Class<K> dto) {
        return mapper.map(entity, dto);
    }

    public TelegramUser apiTelegramUserToEntity(org.telegram.telegrambots.meta.api.objects.User user) {
        return TelegramUser.builder()
                .telegramUserId(user.getId())
                .isActive(true)
                .build();
    }

    public TelegramUserDTO apiTelegramUserEntityToDto(User userEntityTG) {
        return null;
    }

    public <T, K> K convertIncludeObjectToEntity (T includeObject, Class<K> entityClass) {
        return mapper.map(includeObject, entityClass);
    }

}
