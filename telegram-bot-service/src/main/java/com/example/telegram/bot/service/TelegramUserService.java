package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.entity.TelegramUser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import lombok.*;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@ToString
public class TelegramUserService {

    @ToString.Exclude
    private final DataProviderClient dataProviderClient;
    private final TelegramChatService telegramChatService;
    private final TelegramSessionService telegramSessionService;
    private final ModelMapper modelMapper;

    public Mono<ApiResponse<TelegramUserDTO>> getById(long id) {
        return dataProviderClient.getTelegramUserById(id);
    }

}
