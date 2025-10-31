package com.example.telegram.bot.service;

import com.example.data.models.entity.User;
import com.example.data.models.entity.VerificationCode;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.exception.ExpiredVerificationCodeException;
import com.example.data.models.exception.NotValidVerificationCodeException;
import com.example.data.models.service.ModelMapperService;
import com.example.telegram.api.clients.DataProviderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
        code.setActive(true);

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
    public Mono<Boolean> checkCode(Long telegramUserId, String codeForCheck) {

        return getByTelegramUserId(telegramUserId)
                .flatMap(code -> {

                    if (!checkValidation(codeForCheck, code))
                        return Mono.error(new NotValidVerificationCodeException(codeForCheck + " is not valid"));

                    if (!checkExpiration(code))
                        return Mono.error(
                                new ExpiredVerificationCodeException("Verification code " + code.getOtpHash() + " is expired")
                        );

                    return Mono.just(true);
                });

    }
    private boolean checkValidation(String codeForCheck, VerificationCode code) {
        return encoder.matches(codeForCheck, code.getOtpHash());
    }
    private boolean checkExpiration(VerificationCode code) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of(zoneId));

        log.debug("Текущая дата = {}", nowDateTime.format(formatter));
        log.debug("Срок действия кода до = {}", code.getExpiresAt().format(formatter));

        return code.getExpiresAt().isAfter(nowDateTime);
    }

}
