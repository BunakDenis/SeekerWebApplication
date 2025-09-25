package com.example.telegram.bot.service;

import com.example.data.models.entity.PersistentSession;
import com.example.data.models.entity.telegram.PersistentSessionDTO;
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
public class PersistentSessionService {

    private final DataProviderClient dataClient;
    private final ModelMapperService mapperService;

    public Mono<PersistentSession> save(PersistentSession session) {

        PersistentSessionDTO dto = mapperService.toDTO(session, PersistentSessionDTO.class);

        return dataClient.savePersistentSession(dto)
                .flatMap(resp -> {

                    if (Objects.nonNull(resp.getData())) {
                        PersistentSessionDTO respDTO = resp.getData();

                        return Mono.just(mapperService.toEntity(respDTO, PersistentSession.class));
                    } else {
                        return Mono.empty();
                    }
                });

    }
    public Mono<PersistentSession> update(PersistentSession session) {

        PersistentSessionDTO dto = mapperService.toDTO(session, PersistentSessionDTO.class);

        return dataClient.savePersistentSession(dto)
                .flatMap(resp -> {

                    if (Objects.nonNull(resp.getData())) {
                        PersistentSessionDTO respDTO = resp.getData();

                        return Mono.just(mapperService.toEntity(respDTO, PersistentSession.class));
                    } else {
                        return Mono.empty();
                    }
                });
    }
    public Mono<PersistentSession> getActiveSessionByTGUserId(Long id) {

        return dataClient.getTelegramSessionByTelegramUserId(id)
                .flatMap(resp -> {

                    List<PersistentSession> persistentSessions = new ArrayList<>();

                    resp.getIncludedListObjects(ResponseIncludeDataKeys.PERSISTENT_SESSION.getKeyValue())
                            .forEach(prSession -> {

                                PersistentSession session = mapperService.toEntity(prSession, PersistentSession.class);

                                if (session.isActive()) persistentSessions.add(session);

                            });

                    return Mono.just(persistentSessions.get(persistentSessions.size() - 1));

                });
    }
    public Mono<Boolean> delete(Long id) {
        return dataClient.deletePersistentSession(id)
                .flatMap(resp -> Mono.just(resp.getData()));
    }

}
