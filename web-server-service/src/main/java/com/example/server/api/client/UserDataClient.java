package com.example.server.api.client;


import com.example.data.models.entity.User;
import com.example.data.models.entity.UserDetails;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.example.data.models.consts.DataProviderEndpointsConsts.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataClient extends DataProvideClientBaseClass {

    private static final ParameterizedTypeReference<ApiResponse<UserDTO>> USER_TYPE_REF =
            new ParameterizedTypeReference<>() {};
    private final ObjectMapper objectMapper;


    public Mono<UserDTO> save(UserDTO dto) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("add/"));

        ApiRequest<UserDTO> request = new ApiRequest<>(dto);

        return sendPostRequest(endpoint.toString(), request)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<UserDTO> update(UserDTO dto) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("update/"));

        ApiRequest<UserDTO> request = new ApiRequest<>(dto);

        return sendPostRequest(endpoint.toString(), request)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<UserDTO> getById(Long id) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("id/"));
        endpoint.append(id);

        return sendGetRequest(endpoint.toString(), USER_TYPE_REF)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public Mono<User> getByIdWithUserDetails(Long id) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("user_details/id/"));
        endpoint.append(id);

        return sendGetRequest(endpoint.toString(), USER_TYPE_REF)
                .flatMap(resp -> {
                    log.debug("response with user details {}", resp);

                    User user = objectMapper.convertValue(resp.getData(), User.class);
                    UserDetails userDetails = objectMapper.convertValue(
                            resp.getIncludedObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue()),
                            UserDetails.class
                    );
                    user.setUserDetails(userDetails);

                    return Mono.just(user);
                });
    }
    public Mono<Boolean> deleteById(Long id) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("delete/"));
        endpoint.append(id);

        return sendPostRequestWithBooleanResponse(endpoint.toString())
                .flatMap(resp -> Mono.just(resp.getData()));
    }

}
