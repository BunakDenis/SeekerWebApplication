package com.example.telegram.bot.service;

import com.example.data.models.entity.PersistentSession;
import com.example.data.models.entity.TelegramSession;
import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.TransientSession;
import com.example.data.models.entity.jwt.JwtTelegramDataImpl;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.enums.JWTDataSubjectKeys;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.service.JWTService;
import com.example.data.models.service.ModelMapperService;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.utils.datetime.DateTimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Data
@RequiredArgsConstructor
@ToString
@Slf4j
public class TelegramSessionService {


    @Value("${default.utc.zone.id}")
    private String zoneId;
    @Value("${persistent.auth.expiration.time}")
    private long persistentSessionExpirationTime;
    @Value("${transient.auth.expiration.time}")
    private long transientSessionExpirationTime;
    private final DataProviderClient dataProviderClient;
    private final PersistentSessionService prSessionService;
    private final TransientSessionService trSessionService;
    private final JWTService jwtService;
    private final ModelMapperService modelMapperService;
    private final ObjectMapper objectMapper;


    public Mono<TelegramSession> save(TelegramSession telegramSession) {

        return dataProviderClient.saveTelegramSession(telegramSession)
                .flatMap(session -> {

                    log.debug("Telegram session {}", session);

                    TelegramSession savedTelegramSession =
                            modelMapperService.toEntity(session.getData(), TelegramSession.class);

                    TelegramUser telegramUser = modelMapperService.toEntity(
                            session.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                            TelegramUser.class
                    );

                    savedTelegramSession.setTelegramUser(telegramUser);

                    log.debug("Telegram session {}", savedTelegramSession);

                    return Mono.just(savedTelegramSession);
                });
    }
    public Mono<TelegramSession> update(TelegramSession telegramSession) {
        return save(telegramSession);
    }
    public Mono<TelegramSession> getFullByTelegramUserId(Long telegramUserId) {

        return dataProviderClient.getTelegramSessionByTelegramUserId(telegramUserId)
                .flatMap(resp -> {

                    log.debug("Ответ от dataProviderClient {}", resp);

                    TelegramSessionDTO sessionDto = resp.getData();
                    TelegramSession telegramSession = modelMapperService.toEntity(sessionDto, TelegramSession.class);

                    List<TransientSession> transientSessions = new ArrayList<>();
                    List<PersistentSession> persistentSessions = new ArrayList<>();

                    TelegramUser telegramUser = objectMapper.convertValue(
                            resp.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                            TelegramUser.class
                    );

                    resp.getIncludedListObjects(ResponseIncludeDataKeys.TRANSIENT_SESSION.getKeyValue())
                            .forEach(trSessionDTO -> {

                                TransientSession session =
                                        objectMapper.convertValue(trSessionDTO, TransientSession.class);

                                if (session.isActive())
                                    transientSessions.add(session);

                            });

                    resp.getIncludedListObjects(ResponseIncludeDataKeys.PERSISTENT_SESSION.getKeyValue())
                            .forEach(prSessionDTO -> {


                                PersistentSession session =
                                        objectMapper.convertValue(prSessionDTO, PersistentSession.class);

                                if (session.isActive())
                                    persistentSessions.add(session);

                            });

                    telegramSession.setTelegramUser(telegramUser);
                    telegramSession.setTransientSessions(transientSessions);
                    telegramSession.setPersistentSessions(persistentSessions);

                    return Mono.just(telegramSession);
                });
    }
    public Mono<Boolean> checkSessionsExpired(Long tgUserId, UserDetails userDetails) {
        return getFullByTelegramUserId(tgUserId)
                .flatMap(session -> {

                    checkPersistentSession(userDetails, session);

                    return checkPersistentSession(userDetails, session)
                            .flatMap(isPrSessionExpired -> Mono.zip(
                                    Mono.just(session),
                                    Mono.just(isPrSessionExpired)
                            ));
                })
                .flatMap(tuple -> {

                    TelegramSession tgSession = tuple.getT1();
                    Boolean isPrSessionExpired = tuple.getT2();

                    return Boolean.TRUE.equals(isPrSessionExpired) ?
                            Mono.just(false) :
                            checkTransientSession(userDetails, tgSession);
                });
        /*
                .doOnError(err -> log.debug("Ошибка получения Telegram session {}", err.getMessage()))
                .onErrorResume(err -> Mono.just(false));

         */
    }
    private Mono<Boolean> checkPersistentSession(UserDetails userDetails, TelegramSession session) {

        List<PersistentSession> persistentSessions = session.getPersistentSessions();

        PersistentSession persistentSession = persistentSessions.get(persistentSessions.size() - 1);

        String prToken = persistentSession.getData();

        return Mono.just(jwtService.validateToken(prToken, userDetails))
                .flatMap(isExpired -> {

                    if (!isExpired) {
                        LocalDateTime expiration = jwtService.extractExpiration(prToken);

                        LocalDateTime updatedPeriod = LocalDateTime.now().plusDays(1L);

                        if (expiration.isAfter(updatedPeriod)) {
                            JwtTelegramDataImpl jwtData = JwtTelegramDataImpl.builder()
                                    .userDetails(userDetails)
                                    .expirationTime(
                                            DateTimeService.convertMinutesToMillis(persistentSessionExpirationTime)
                                    )
                                    .subjects(
                                            Map.of(
                                                    JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(),
                                                    session.getTelegramUser().getTelegramUserId()
                                            )
                                    )
                                    .build();

                            return Mono.just(jwtService.generateToken(jwtData))
                                    .flatMap(token -> {

                                        PersistentSession newPersistentSession = PersistentSession.builder()
                                                .telegramSession(persistentSession.getTelegramSession())
                                                .active(true)
                                                .data(token)
                                                .build();

                                        persistentSession.setActive(false);

                                        return prSessionService.update(persistentSession)
                                                .then(prSessionService.save(newPersistentSession));
                                    })
                                    .flatMap(resp -> Mono.just(true));
                        } else {
                            return Mono.just(true);
                        }
                    }

                    persistentSession.setActive(false);

                    return prSessionService.update(persistentSession)
                            .then(Mono.just(false));

                })
                .doOnError(err -> log.error("Ошибка проверки долгосрочного токена {}", err.getMessage(), err))
                .onErrorResume(err -> {

                    persistentSession.setActive(false);

                    return prSessionService.update(persistentSession)
                            .then(Mono.just(false));
                });
    }
    private Mono<Boolean> checkTransientSession(UserDetails userDetails, TelegramSession session) {

        List<TransientSession> transientSessions = session.getTransientSessions();

        TransientSession transientSession = transientSessions.get(transientSessions.size() - 1);

        String trToken = transientSession.getData();

        return Mono.just(jwtService.validateToken(trToken, userDetails))
                .flatMap(isExpired -> {

                    JwtTelegramDataImpl jwtData = JwtTelegramDataImpl.builder()
                            .userDetails(userDetails)
                            .expirationTime(
                                    DateTimeService.convertDaysToMillis(persistentSessionExpirationTime)
                            )
                            .subjects(
                                    Map.of(
                                            JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(),
                                            session.getTelegramUser().getTelegramUserId()
                                    )
                            )
                            .build();

                    return Mono.just(jwtService.generateToken(jwtData))
                            .flatMap(token -> {

                                transientSession.setActive(false);

                                TransientSession newTransientSession = TransientSession.builder()
                                        .telegramSession(session)
                                        .data(token)
                                        .active(true)
                                        .build();

                                return trSessionService.update(transientSession)
                                        .then(trSessionService.save(newTransientSession));

                            })
                            .then(Mono.just(true));
                })
                .doOnError(err -> log.error("Ошибка проверки временного токена {}", err.getMessage()))
                .onErrorResume(err -> {
                    transientSession.setActive(false);

                    return trSessionService.update(transientSession)
                            .then(Mono.just(false));
                });

    }

}