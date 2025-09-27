package com.example.data.models.utils.generator;


import com.example.data.models.entity.jwt.JwtTelegramDataImpl;
import com.example.data.models.enums.JWTDataSubjectKeys;
import com.example.data.models.service.JWTService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@RequiredArgsConstructor
public class TransientSessionGenerator {


    @Value("${transient.auth.expiration.time}")
    private long transientSessionExpirationTime;
    private final JWTService jwtService;

    protected String generate(UserDetails userDetails, Long telegramUserId) {

        JwtTelegramDataImpl jwtTelegramData = JwtTelegramDataImpl.builder()
                .expirationTime(transientSessionExpirationTime)
                .userDetails(userDetails)
                .subjects(
                        Map.of(JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(), telegramUserId)
                )
                .build();

        return jwtService.generateToken(jwtTelegramData);
    }

}
