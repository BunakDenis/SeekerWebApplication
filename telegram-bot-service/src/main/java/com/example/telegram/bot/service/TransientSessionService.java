package com.example.telegram.bot.service;


import com.example.data.models.entity.TransientSession;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.telegram.api.clients.DataProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransientSessionService {

    private final DataProviderClient dataClient;

    private final ModelMapperService mapperService;

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
