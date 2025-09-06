package com.example.telegram.bot.service;

import com.example.data.models.entity.PersistentSession;
import com.example.data.models.entity.TelegramSession;
import com.example.data.models.entity.TransientSession;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.telegram.api.clients.DataProviderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.management.MonitorInfo;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@ToString
public class TelegramSessionService {

    private final DataProviderClient dataProviderClient;
    private final ModelMapperService modelMapperService;
    private final ObjectMapper objectMapper;

    public Mono<TelegramSession> save(TelegramSession telegramSession) {

        TelegramSessionDTO dto = modelMapperService.toDTO(telegramSession, TelegramSessionDTO.class);

        return dataProviderClient.createTelegramSession(dto)
                .flatMap(session -> Mono.just(modelMapperService.toEntity(session, TelegramSession.class)));
    }

    public Mono<TelegramSession> get(Long telegramUserId) {

        return dataProviderClient.getTelegramSessionByTelegramUserId(telegramUserId)
                .flatMap(resp -> {

                    TelegramSessionDTO sessionDto = resp.getData();
                    TelegramSession telegramSession = modelMapperService.toEntity(sessionDto, TelegramSession.class);

                    List<TransientSession> transientSessions = new ArrayList<>();
                    List<PersistentSession> persistentSessions = new ArrayList<>();

                    resp.getIncludedListObjects(ResponseIncludeDataKeys.TRANSIENT_SESSION.getKeyValue())
                            .forEach(trSessionDTO ->
                                    transientSessions.add(modelMapperService.toEntity(
                                                    trSessionDTO, TransientSession.class
                                            )
                                    )
                            );

                    resp.getIncludedListObjects(ResponseIncludeDataKeys.PERSISTENT_SESSION.getKeyValue())
                            .forEach(prSessionDTO ->
                                    persistentSessions.add(modelMapperService.toEntity(
                                                    prSessionDTO, PersistentSession.class
                                            )
                                    )
                            );
                    telegramSession.setTransientSessions(transientSessions);
                    telegramSession.setPersistentSessions(persistentSessions);

                    return Mono.just(telegramSession);
                });
    }

}
