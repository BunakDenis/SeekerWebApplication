package com.example.data.models.utils.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;;

@Component
@RequiredArgsConstructor
public class GenerationService {

    private final VerificationCodeGenerator verificationCodeGenerator;
    private final TransientSessionGenerator transientSessionGenerator;
    private final PersistentSessionGenerator persistentSessionGenerator;

    public String generateEmailVerificationCode() {
        return verificationCodeGenerator.generateVerificationCode();
    }
    public String generateTransientSessionDataToken(UserDetails userDetails, Long telegramUserId) {
        return transientSessionGenerator.generate(userDetails, telegramUserId);
    }
    public String generatePersistentSessionDataToken(UserDetails userDetails, Long telegramUserId) {
        return persistentSessionGenerator.generatePersistentDataToken(userDetails, telegramUserId);
    }

}
