package com.example.data.models.utils.generator;


import com.example.data.models.entity.jwt.JwtData;
import com.example.data.models.entity.jwt.JwtTelegramDataImpl;
import com.example.data.models.enums.JWTDataSubjectKeys;
import com.example.data.models.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PersistentSessionGenerator {

    private final JWTService jwtService;

    @Value("${persistent.auth.expiration.time}")
    private long persistentSessionExpirationTime;

    protected String generatePersistentDataToken(UserDetails userDetails, Long telegramUserId) {
        JwtTelegramDataImpl data = JwtTelegramDataImpl.builder()
                .expirationTime(persistentSessionExpirationTime)
                .userDetails(userDetails)
                .subjects(
                        Map.of(JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(), telegramUserId)
                )
                .build();

        return jwtService.generateToken(data);
    }

}
