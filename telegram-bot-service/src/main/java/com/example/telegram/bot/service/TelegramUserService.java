package com.example.telegram.bot.service;

import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.telegram.TelegramUserDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.telegram.api.clients.DataProviderClient;
import org.springframework.stereotype.Service;
import lombok.*;
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

    public Mono<TelegramUser> save(TelegramUser telegramUser) {

        TelegramUserDTO dto = mapperService.toDTO(telegramUser, TelegramUserDTO.class);

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
    public Mono<TelegramUser> getById(long id) {
        return dataProviderClient.getTelegramUserByTelegramUserId(id)
                .flatMap(resp -> {

                    if (Objects.isNull(resp.getData())) {
                        return Mono.error(
                                new EntityNotFoundException("Telegram user with id=" + id + ", is not found",
                                        new TelegramUser())
                        );
                    }

                    TelegramUser result = mapperService.toEntity(resp.getData(), TelegramUser.class);

                    return Mono.just(result);
                });
    }
    public Mono<Boolean> delete(Long id) {
        return dataProviderClient.deleteTelegramUser(id)
                .flatMap(resp -> Mono.just(resp.getData()));
    }

}
