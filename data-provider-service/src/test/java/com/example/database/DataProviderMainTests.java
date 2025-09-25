package com.example.database;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.utils.ApiResponseUtilsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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

        client.get()
                .uri("/api/v100/user/id/500")
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse<?> body = resp.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.getMessage().contains("No static resource"));
                });

    }


}
