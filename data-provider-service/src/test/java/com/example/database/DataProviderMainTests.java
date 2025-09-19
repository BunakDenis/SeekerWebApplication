package com.example.database;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.utils.ApiResponseUtilsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class DataProviderMainTests extends DataProviderTestsBaseClass {

    @Test
    void testResponseWithoutApiKeyHeader() throws Exception {

        //Given
        ApiResponse<Object> expectedResponse = ApiResponseUtilsService.fail(
                ResponseMessageProvider.REQUEST_DO_NOT_CONTAIN_API_KEY
        );

        //Then
        client.post()
                .uri("/api/v1/user/id/5")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .json(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testResponseToUnknownEndPoint() {

        String expectedExceptionMsg = "404 NOT_FOUND \"No static resource api/v100/user/id/500.\"";

        ApiResponse response = client.get()
                .uri("/api/v100/user/id/500")
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .returnResult()
                .getResponseBody();

        log.debug("Response = {}", response);

        assertEquals(expectedExceptionMsg, response.getMessage());

    }

}
