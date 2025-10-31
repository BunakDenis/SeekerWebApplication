package com.example.server.api.client;


import com.example.data.models.entity.VerificationCode;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.service.ModelMapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.example.data.models.consts.DataProviderEndpointsConsts.getApiVerificationCodeEndpoint;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeDataClient extends DataProvideClientBaseClass {


    private static final ParameterizedTypeReference<ApiResponse<VerificationCodeDTO>> VERIFICATION_CODE_TYPE_REF =
            new ParameterizedTypeReference<>() {};
    private final ModelMapperService mapperService;
    private final ObjectMapper objectMapper;

    public Mono<VerificationCodeDTO> save(VerificationCode code) {
        StringBuilder endpoint = new StringBuilder(getApiVerificationCodeEndpoint("add/"));
        VerificationCodeDTO dto = mapperService.toDTO(code, VerificationCodeDTO.class);
        UserDTO userDTO = mapperService.toDTO(code.getUser(), UserDTO.class);

        ApiRequest<VerificationCodeDTO> request = new ApiRequest(dto);
        request.addIncludeObject(ResponseIncludeDataKeys.USER.getKeyValue(), userDTO);

        return sendPostRequest(endpoint.toString(), request, VERIFICATION_CODE_TYPE_REF)
                .flatMap(resp -> {
                    log.debug("Ответ от data provider service {}", resp);
                    VerificationCodeDTO savedDto = resp.getData();
                    return Mono.just(savedDto);
                });
    }
    public Mono<VerificationCodeDTO> update(VerificationCodeDTO dto) {
        StringBuilder endpoint = new StringBuilder(getApiVerificationCodeEndpoint("update/"));

        ApiRequest request = new ApiRequest(dto);

        return sendPostRequest(endpoint.toString(), request, VERIFICATION_CODE_TYPE_REF)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<VerificationCodeDTO> getById(Long id) {
        StringBuilder endpoint = new StringBuilder(getApiVerificationCodeEndpoint(Long.toString(id)));

        return sendGetRequest(endpoint.toString(), VERIFICATION_CODE_TYPE_REF)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<VerificationCodeDTO> getByOtpHash(String otpHash) {
        StringBuilder endpoint = new StringBuilder(getApiVerificationCodeEndpoint("otp_hash/" + otpHash));

        return sendGetRequest(endpoint.toString(), VERIFICATION_CODE_TYPE_REF)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<Boolean> deleteById(Long id) {
        StringBuilder endpoint = new StringBuilder(getApiVerificationCodeEndpoint("delete/"));
        endpoint.append(id);

        return sendPostRequestWithBooleanResponse(endpoint.toString())
                .flatMap(resp -> Mono.just(resp.getData()));
    }

}
