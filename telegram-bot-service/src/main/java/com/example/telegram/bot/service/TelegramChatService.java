package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.entity.TelegramUser;
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
        return dataProviderClient.saveTelegramChat(chat)
                .flatMap(resp -> {
                    if (resp.getData() instanceof TelegramChatDTO) {
                        return Mono.just(toEntity(resp));
                    } else {
                        throw new EntityNotSavedException("Телеграм чат с id " + chat.getId() + ", не сохранён");
                    }
                });
    }
    public Mono<TelegramChat> getTelegramChatById(Long id) {

        return dataProviderClient.getTelegramChat(id)
                .flatMap(resp -> {
                    if (resp.getData() instanceof TelegramChatDTO) {
                        return Mono.just(toEntity(resp));
                    } else {
                        throw new EntityNotFoundException("Телеграм чат с id " + id + ", не найден");
                    }
                });

    }

    public Mono<TelegramChat> getTelegramChatByIdWithTelegramUser(Long id) {

        return dataProviderClient.getTelegramChatWithTelegramUser(id)
                .flatMap(resp -> {
                    if (resp.getData() instanceof TelegramChatDTO) {
                        return Mono.just(toEntity(resp));
                    } else {
                        log.debug("Телеграм чат с id " + id + ", не найден");
                        throw new EntityNotFoundException("Телеграм чат с id " + id + ", не найден");
                    }
                });

    }

    public TelegramChat toEntity(ApiResponse<TelegramChatDTO> respWithDto) {

        log.debug(respWithDto.toString());

        TelegramChatDTO dto = respWithDto.getData();

        TelegramChat telegramChat = mapperService.toEntity(dto, TelegramChat.class);

        log.debug("respWithDto.getIncluded().isEmpty() = {}", respWithDto.getIncluded().isEmpty());

        if (!respWithDto.getIncluded().isEmpty() && Objects.nonNull(respWithDto.getIncluded())) {
            TelegramUser telegramUser = mapperService.toEntity(
                    respWithDto.getIncludeObject("telegram_user"),
                    TelegramUser.class
            );

            log.debug(telegramUser.toString());

            if (Objects.nonNull(telegramUser)) {
                telegramChat.setTelegramUser(telegramUser);
            }
        }

        log.debug("Метод TelegramChat toEntity, результат {}", telegramChat);

        return telegramChat;
    }


}
