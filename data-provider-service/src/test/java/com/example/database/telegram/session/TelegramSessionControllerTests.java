package com.example.database.telegram.session;

import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.database.DataProviderTestsBaseClass;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.service.telegram.TelegramSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LinkedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Map;

import static com.example.database.constants.UserConstantsForTests.*;
import static com.example.data.models.consts.DataProviderEndpointsConsts.*;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
public class TelegramSessionControllerTests extends DataProviderTestsBaseClass {

    @Autowired
    private TelegramSessionService telegramSessionService;

    private Map<String, ApiRequest<?>> requests;
    private Map<String, ApiResponse<?>> responses;


    @BeforeEach
    void init() {

        requests = new LinkedMap();
        responses = new LinkedMap();

        //Response Map initialization
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(TELEGRAM_USER_FOR_TESTS, TelegramUserDTO.class);
        TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(TELEGRAM_SESSION_FOR_TESTS, TelegramSessionDTO.class);

        ApiResponse<TelegramUserDTO> telegramUserResponse = ApiResponse.<TelegramUserDTO>builder()
                .status(HttpStatus.OK)
                .data(telegramUserDTO)
                .build();

        ApiResponse<TelegramSessionDTO> telegramSessionResponse = ApiResponse.<TelegramSessionDTO>builder()
                .status(HttpStatus.OK)
                .data(telegramSessionDTO)
                .build();

        responses.put(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserResponse);
        responses.put(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionResponse);

        //Request Map initialization

        telegramUserDTO.setId(null);
        telegramSessionDTO.setId(null);

        ApiRequest<TelegramUserDTO> telegramUserRequest = new ApiRequest<>(telegramUserDTO);
        ApiRequest<TelegramSessionDTO> telegramSessionRequest = new ApiRequest<>(telegramSessionDTO);

        requests.put(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserRequest);
        requests.put(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionRequest);

    }


    @Test
    public void testSaveTelegramSession() {

        ApiRequest<?> telegramSessionRequest = requests.get(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue());

        ParameterizedTypeReference<ApiResponse> apiResponseType = new ParameterizedTypeReference<>() {
        };

        EntityExchangeResult<ApiResponse> getTelegramUserResult =
                client.get().uri(dataProviderEndpoint + getApiTelegramUserEndpoint(
                                        Long.toString(TELEGRAM_USER_FOR_TESTS.getTelegramUserId())
                                )
                        )
                        .header(apiKeyHeaderName, "apiHeader")
                        .exchange()
                        .expectBody(ApiResponse.class)
                        .returnResult();

        ApiResponse getTelegramUserResultResponseBody = getTelegramUserResult.getResponseBody();

        assertNotNull(getTelegramUserResultResponseBody.getData());

        telegramSessionRequest.addIncludeObject(
                ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(),
                getTelegramUserResultResponseBody.getData()
        );

        log.debug("telegramSessionRequest = {}", telegramSessionRequest);

        client.post().uri(dataProviderEndpoint + getApiSessionEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(telegramSessionRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(response -> {

                    ApiResponse apiResponse = response.getResponseBody();

                    assertNotNull(apiResponse.getData());

                });
}

}

