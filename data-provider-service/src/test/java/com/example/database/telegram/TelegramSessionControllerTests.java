package com.example.database.telegram;

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

        EntityExchangeResult<ApiResponse> getTelegramUserResult =
                sessionGetExchangeResultByTelegramUserId(TELEGRAM_USER_FOR_TESTS.getTelegramUserId());


        ApiResponse getTelegramUserResultResponseBody = getTelegramUserResult.getResponseBody();
        TelegramUserDTO telegramUserDTO = objectMapper.convertValue(
                getTelegramUserResultResponseBody.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                TelegramUserDTO.class
        );

        assertNotNull(getTelegramUserResultResponseBody.getData());

        telegramSessionRequest.addIncludeObject(
                ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(),
                telegramUserDTO
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
    @Test
    public void testUpdateTelegramSession() {

        //Given
        String expectedSessionDataText = "New session data";
        EntityExchangeResult<ApiResponse> getSessionExchangeResult =
                sessionGetExchangeResultByTelegramUserId(TELEGRAM_USER_FOR_TESTS.getTelegramUserId());

        ApiResponse sessionGetApiResponse = getSessionExchangeResult.getResponseBody();

        TelegramSessionDTO expectTelegramSession =
                objectMapper.convertValue(sessionGetApiResponse.getData(), TelegramSessionDTO.class);

        TelegramUserDTO telegramUserDTO = objectMapper.convertValue(
                sessionGetApiResponse.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                TelegramUserDTO.class
        );

        expectTelegramSession.setSessionData(expectedSessionDataText);

        ApiRequest updateRequest = new ApiRequest(expectTelegramSession);
        updateRequest.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

        EntityExchangeResult<ApiResponse> sessionUpdateExchangeResult =
                client.post().uri(dataProviderEndpoint + getApiSessionEndpoint("update/"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(apiKeyHeaderName, "apiHeader")
                        .bodyValue(updateRequest)
                        .exchange()
                        .expectStatus().is2xxSuccessful()
                        .expectBody(ApiResponse.class)
                        .returnResult();

        ApiResponse sessionUpdateResponse = sessionUpdateExchangeResult.getResponseBody();

        assertNotNull(sessionUpdateResponse.getData());

        TelegramSession actualTelegramSession =
                objectMapper.convertValue(sessionUpdateResponse.getData(), TelegramSession.class);

        assertEquals(expectedSessionDataText, actualTelegramSession.getSessionData());

    }
    @Test
    public void testGetTelegramSession() {

        //Given
        String id = "1";

        //Then
        client.get().uri(dataProviderEndpoint + getApiSessionEndpoint(id))
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(response -> {

                    ApiResponse apiResponse = response.getResponseBody();

                    assertNotNull(apiResponse.getData());

                    TelegramSessionDTO telegramSessionDTO = objectMapper.convertValue(apiResponse.getData(), TelegramSessionDTO.class);

                    assertEquals(id, Long.toString(telegramSessionDTO.getId()));
                });
    }
    @Test
    public void testDeleteTelegramSession() {

        TelegramSessionDTO telegramSessionDTO = TelegramSessionDTO.builder()
                .sessionData("Test delete method")
                .build();

        EntityExchangeResult<ApiResponse> telegramUserForRequest =
                telegramUserGetExchangeResultById(TELEGRAM_USER_FOR_TESTS.getTelegramUserId());

        TelegramUserDTO telegramUserDTO =
                objectMapper.convertValue(telegramUserForRequest.getResponseBody().getData(), TelegramUserDTO.class);

        ApiRequest sessionSavedRequest = new ApiRequest(telegramSessionDTO);
        sessionSavedRequest.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

        EntityExchangeResult<ApiResponse> savedExchangeResult = client.post().uri(dataProviderEndpoint + getApiSessionEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(sessionSavedRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .returnResult();

        ApiResponse savedExchangeResultResponseBody = savedExchangeResult.getResponseBody();

        TelegramSessionDTO savedTelegramSessionDTO =
                objectMapper.convertValue(savedExchangeResultResponseBody.getData(), TelegramSessionDTO.class);

        client.post()
                .uri(dataProviderEndpoint + getApiSessionEndpoint("delete/" + savedTelegramSessionDTO.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(response -> {
                    ApiResponse responseBody = response.getResponseBody();

                    Boolean expectedSessionDeleteResult =
                            objectMapper.convertValue(responseBody.getData(), Boolean.class);

                    assertTrue(expectedSessionDeleteResult);
                });
    }

    private EntityExchangeResult<ApiResponse> sessionGetExchangeResultByTelegramUserId(Long telegramUserId) {
        return client.get().uri(
                        dataProviderEndpoint + getApiSessionEndpoint(
                                "telegram_user_id/" + telegramUserId
                        )
                )
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .returnResult();
    }
    private EntityExchangeResult<ApiResponse> telegramUserGetExchangeResultById(Long telegramUserId) {
        return client.get().uri(
                        dataProviderEndpoint + getApiTelegramUserEndpoint(
                                Long.toString(telegramUserId)
                        )
                )
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectBody(ApiResponse.class)
                .returnResult();
    }

}

