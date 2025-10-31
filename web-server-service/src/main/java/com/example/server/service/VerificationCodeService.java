package com.example.server.service;


import com.example.data.models.entity.VerificationCode;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.service.ModelMapperService;
import com.example.server.api.client.VerificationCodeDataClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {

    private final VerificationCodeDataClient client;
    private final ModelMapperService mapperService;

    public Mono<VerificationCode> save(VerificationCode code) {
        return client.save(code)
                .flatMap(resp ->
                        Mono.just(
                                mapperService.toEntity(resp, VerificationCode.class)
                        )
                );
    }
    public Mono<VerificationCode> update(VerificationCodeDTO dto) {
        return client.update(dto)
                .flatMap(resp ->
                        Mono.just(
                                mapperService.toEntity(resp, VerificationCode.class)
                        )
                );
    }
    public Mono<VerificationCode> getByOtpHash(String otpHash) {
        return client.getByOtpHash(otpHash)
                .flatMap(resp -> Mono.just(mapperService.toEntity(otpHash, VerificationCode.class)));
    }
    public Mono<Boolean> deleteById(Long id) {
        return client.deleteById(id);
    }

}