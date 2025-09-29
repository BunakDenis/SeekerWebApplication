package com.example.telegram.bot.service;

import com.example.data.models.entity.TelegramSession;
import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.User;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.telegram.api.clients.DataProviderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramUserService {

    @ToString.Exclude
    private final DataProviderClient dataProviderClient;
    private final ModelMapperService mapperService;
    private final ObjectMapper objectMapper;

    public Mono<TelegramUser> save(TelegramUser telegramUser) {

        return dataProviderClient.createTelegramUser(telegramUser)
                .flatMap(resp -> {
                            if (Objects.nonNull(resp.getData())) {

                                TelegramUser savedTelegramUser = mapperService.toEntity(resp.getData(), TelegramUser.class);

                                User user = mapperService.convertIncludeObjectToEntity(
                                        resp.getIncludedObject(ResponseIncludeDataKeys.USER.getKeyValue()),
                                        User.class
                                );
                                user.setTelegramUsers(List.of(savedTelegramUser));

                                savedTelegramUser.setUser(user);

                                return Mono.just(savedTelegramUser);
                            }
                            return Mono.empty();
                        }
                )
                .doOnError(err -> log.error("Ошибка записи telegram user {}", telegramUser, err))
                .onErrorReturn(new TelegramUser());
    }
    public Mono<TelegramUser> update(TelegramUserDTO dto) {
        return dataProviderClient.updateTelegramUser(dto)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                        Mono.just(mapperService.toEntity(resp.getData(), TelegramUser.class)) :
                        Mono.empty()
                );
    }
    public Mono<TelegramUser> getByTelegramUserId(long id) {
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
    public Mono<TelegramUser> getByTelegramUserIdWithTelegramSession(long id) {
        return dataProviderClient.getTelegramUserByTelegramUserIdWithTelegramSession(id)
                .flatMap(resp -> {

                    if (Objects.isNull(resp.getData())) {
                        return Mono.error(
                                new EntityNotFoundException("Telegram user with id=" + id + ", is not found",
                                        new TelegramUser())
                        );
                    }

                    TelegramUser result = mapperService.toEntity(resp.getData(), TelegramUser.class);
                    TelegramSessionDTO telegramSessionDTO = objectMapper.convertValue(
                            resp.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue()),
                            TelegramSessionDTO.class
                    );

                    log.debug("telegramSessionDTO = {}", telegramSessionDTO);

                    TelegramSession telegramSession = mapperService.toEntity(
                            telegramSessionDTO,
                            TelegramSession.class
                    );

                    result.setTelegramSessions(List.of(telegramSession));

                    log.debug("Telegram user = {}", result);

                    return Mono.just(result);
                });
    }
    public Mono<Boolean> delete(Long id) {
        return dataProviderClient.deleteTelegramUser(id)
                .flatMap(resp -> Mono.just(resp.getData()));
    }

}
