package com.example.telegram.bot.service;


import com.example.data.models.entity.TransientSession;
import com.example.data.models.entity.telegram.TransientSessionDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.telegram.api.clients.DataProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransientSessionService {

    private final DataProviderClient dataClient;

    private final ModelMapperService mapperService;

    public Mono<TransientSession> save(TransientSession session) {

        TransientSessionDTO dto = mapperService.toDTO(session, TransientSessionDTO.class);

        return dataClient.saveTransientSession(dto)
                .flatMap(resp -> {

                    if (Objects.nonNull(resp.getData())) {
                        TransientSessionDTO respDTO = resp.getData();

                        return Mono.just(mapperService.toEntity(respDTO, TransientSession.class));
                    } else {
                        return Mono.empty();
                    }
                });

    }
    public Mono<TransientSession> update(TransientSession session) {

        TransientSessionDTO dto = mapperService.toDTO(session, TransientSessionDTO.class);

        return dataClient.saveTransientSession(dto)
                .flatMap(resp -> {

                    if (Objects.nonNull(resp.getData())) {
                        TransientSessionDTO respDTO = resp.getData();

                        return Mono.just(mapperService.toEntity(respDTO, TransientSession.class));
                    } else {
                        return Mono.empty();
                    }
                });
    }
    public Mono<Boolean> delete(Long id) {
        return dataClient.deleteTransientSession(id)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<TransientSession> getActiveSessionByTGUserId(Long id) {

        return dataClient.getTelegramSessionByTelegramUserId(id)
                .flatMap(resp -> {

                    List<TransientSession> transientSessions = new ArrayList<>();

                    resp.getIncludedListObjects(ResponseIncludeDataKeys.TRANSIENT_SESSION.getKeyValue())
                            .forEach(trSession -> {

                                TransientSession session = mapperService.toEntity(trSession, TransientSession.class);

                                if (session.isActive()) transientSessions.add(session);

                            });

                    return Mono.just(transientSessions.get(transientSessions.size() - 1));

                });
    }

}
