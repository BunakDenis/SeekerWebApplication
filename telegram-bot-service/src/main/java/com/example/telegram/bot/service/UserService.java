package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.entity.TelegramUser;
import com.example.telegram.bot.entity.User;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@Builder
@Service
public class UserService {

    private final DataProviderClient dataProviderClient;

    private final ModelMapperService mapperService;

    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserId(Long id) {
        return dataProviderClient.getUserByTelegramUserId(id);
    }

}
