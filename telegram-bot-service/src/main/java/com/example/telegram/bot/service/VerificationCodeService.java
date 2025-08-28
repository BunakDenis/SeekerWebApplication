package com.example.telegram.bot.service;

import com.example.data.models.entity.User;
import com.example.data.models.entity.VerificationCode;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.serialization.model.VerificationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {

    @Value("${default.utc.zone.id}")
    private String zoneId;
    private final DataProviderClient dataProviderClient;
    private final ModelMapperService mapperService;
    private final PasswordEncoder encoder;


    private Mono<VerificationCode> getById(Long id) {
        return dataProviderClient.getVerificationCodeById(id)
                .flatMap(resp -> {

                    if (resp.getData() != null) {
                        return Mono.just(
                              mapperService.toEntity(resp.getData(), VerificationCode.class)
                        );
                    } else {
                        log.error(resp.getMessage());
                        log.error(resp.getDebugMsg());
                        return Mono.just(VerificationCode.builder().build());
                    }
                });
    }
    private Mono<VerificationCode> getByTelegramUserId(Long id) {
        return dataProviderClient.getVerificationCodeByTelegramUserId(id)
                .flatMap(resp -> {
                    if (resp.getData() != null) {
                        return Mono.just(
                                mapperService.toEntity(resp.getData(), VerificationCode.class)
                        );
                    } else {
                        log.error(resp.getMessage());
                        log.error(resp.getDebugMsg());
                        return Mono.just(VerificationCode.builder().build());
                    }
                });
    }
    public Mono<VerificationCode> save(VerificationCode code) {

        log.debug("Сохранение верификационного кода {}", code);

        LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("UTC+2"));
        LocalDateTime expiresAt = createdAt.plusMinutes(5L);

        String hashCode = encoder.encode(code.getOtpHash());
        code.setOtpHash(hashCode);
        code.setCreatedAt(createdAt);
        code.setExpiresAt(expiresAt);

        User user = code.getUser();

        VerificationCodeDTO dto = mapperService.toDTO(code, VerificationCodeDTO.class);

        ApiRequest<VerificationCodeDTO> request = new ApiRequest<>(dto);

        request.addIncludeObject(
                "user",
                mapperService.toDTO(
                        user, UserDTO.class));

        return dataProviderClient.saveVerificationCode(request)
                .flatMap(resp -> {

                    if (resp.getData() != null) {
                        return Mono.just(
                                mapperService.toEntity(resp.getData(), VerificationCode.class)
                        );
                    } else {
                        log.error(resp.getMessage());
                        log.error(resp.getDebugMsg());
                        return Mono.just(VerificationCode.builder().build());
                    }
                });
    }
    public Mono<Boolean> isCodeValid(Long telegramUserId, String codeForCheck) {
        return dataProviderClient.getVerificationCodeByTelegramUserId(telegramUserId)
                .flatMap(resp -> {
                    if (resp.getData() != null) {

                        VerificationCodeDTO code = resp.getData();

                        boolean checkExpirationTime =
                                LocalDateTime.now(ZoneId.of(zoneId)).isAfter(code.getExpiresAt());

                        boolean checkMatch =
                                encoder.matches(code.getOtpHash(), codeForCheck);

                        boolean result = checkExpirationTime && checkMatch;

                        return Mono.just(result);
                    } else {
                        log.error(resp.getMessage());
                        log.error(resp.getDebugMsg());
                        return Mono.just(false);
                    }
                });
    }

}
