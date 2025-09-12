package com.example.telegram.bot.service;

import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.telegram.api.clients.DataProviderClient;
import org.springframework.stereotype.Service;
import lombok.*;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@Data
@RequiredArgsConstructor
@ToString
public class TelegramUserService {

    @ToString.Exclude
    private final DataProviderClient dataProviderClient;
    private final TelegramChatService telegramChatService;
    private final TelegramSessionService telegramSessionService;
    private final ModelMapperService mapperService;

    public Mono<TelegramUser> save(TelegramUserDTO dto) {
        return dataProviderClient.createTelegramUser(dto)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                        Mono.just(mapperService.toEntity(resp.getData(), TelegramUser.class)) :
                        Mono.empty()
                );
    }
    public Mono<TelegramUser> update(TelegramUserDTO dto) {
        return dataProviderClient.updateTelegramUser(dto)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                        Mono.just(mapperService.toEntity(resp.getData(), TelegramUser.class)) :
                        Mono.empty()
                );
    }
    public Mono<ApiResponse<TelegramUserDTO>> getById(long id) {
        return dataProviderClient.getTelegramUserById(id);
    }
    public Mono<Boolean> delete(Long id) {
        return dataProviderClient.deleteTelegramUser(id)
                .flatMap(resp -> Mono.just(resp.getData()));
    }

}
