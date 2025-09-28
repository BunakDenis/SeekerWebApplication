package com.example.telegram.bot.service;

import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.data.models.entity.TelegramChat;
import com.example.data.models.entity.TelegramUser;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@ToString
@Slf4j
public class TelegramChatService {

    private final DataProviderClient dataProviderClient;

    private final ModelMapperService mapperService;

    public Mono<TelegramChat> save(TelegramChat chat) {

        log.debug(chat.toString());
        log.debug(chat.getTelegramUser().toString());

        return dataProviderClient.saveTelegramChat(chat)
                .flatMap(resp -> {
                    if (resp.getData() instanceof TelegramChatDTO) {
                        return Mono.just(toEntity(resp));
                    } else {
                        throw new EntityNotSavedException("Телеграм чат с id " + chat.getId() + ", не сохранён");
                    }
                });
    }
    public Mono<TelegramChat> update(TelegramChat chat) {

        TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);

        return dataProviderClient.updateTelegramChat(dto)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ? Mono.just(mapperService.toEntity(resp.getData(), TelegramChat.class)) :
                    Mono.empty()
                );
    }
    public Mono<TelegramChat> getTelegramChatById(Long id) {

        return dataProviderClient.getTelegramChat(id)
                .flatMap(resp -> {
                    if (resp.getData() instanceof TelegramChatDTO) {
                        return Mono.just(toEntity(resp));
                    } else {
                        throw new EntityNotFoundException("Телеграм чат с id " + id + ", не найден", new TelegramChat());
                    }
                });

    }
    public Mono<TelegramChat> getTelegramChatByIdWithTgUser(Long id) {

        return dataProviderClient.getTelegramChatByIdWithTgUser(id)
                .flatMap(resp -> {
                    if (resp.getData() instanceof TelegramChatDTO) {
                        TelegramChat telegramChat = toEntity(resp);

                        return Mono.just(telegramChat);
                    } else {
                        log.debug("Телеграм чат с id " + id + ", не найден");
                        throw new EntityNotFoundException("Телеграм чат с id " + id + ", не найден",  new TelegramChat());
                    }
                })
                .doOnError(err -> log.error("Ошибка получения telegram chat {}", err))
                .onErrorResume(err -> {
                    if (err instanceof EntityNotFoundException) {
                        return Mono.empty();
                    }
                    return Mono.error(err);
                });

    }
    public Mono<TelegramChat> getTelegramChatByTelegramUserId(Long id) {
        return dataProviderClient.getTelegramChatByTelegramUserId(id)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                        Mono.just(
                                mapperService.toEntity(resp.getData(), TelegramChat.class)
                        ) :
                        Mono.empty());
    }
    public Mono<Boolean> delete(Long id) {
        return dataProviderClient.deleteTelegramChat(id)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                        Mono.just(resp.getData()) :
                        Mono.empty());
    }
    public TelegramChat toEntity(ApiResponse<TelegramChatDTO> respWithDto) {

        TelegramChatDTO dto = respWithDto.getData();

        TelegramChat telegramChat = mapperService.toEntity(dto, TelegramChat.class);

        if (Objects.nonNull(respWithDto.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()))) {
            TelegramUser telegramUser = mapperService.toEntity(
                    respWithDto.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                    TelegramUser.class
            );

            telegramChat.setTelegramUser(telegramUser);
        }
        return telegramChat;
    }


}
