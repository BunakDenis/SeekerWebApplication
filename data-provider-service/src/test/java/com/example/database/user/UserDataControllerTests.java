package com.example.database.user;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.service.JWTService;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.DataProviderTestsBaseClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;


@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UserDataControllerTests extends DataProviderTestsBaseClass {

    @MockBean
    private JWTService jwtService;

    @BeforeAll
    public static void init() {
        log.debug("init");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    @BeforeEach
    public void mockMethodCalls() {
        Mockito.when(jwtService.extractUsername(any())).thenReturn("telegram-bot-service");
    }

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
    void testGetUserWithInvalidId() {

        //Given
        long userId = 5L;

        //When
        ApiResponse response = client.get()
                .uri("/api/v1/user/id/" + userId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .returnResult()
                .getResponseBody();

        //Then
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        Assertions.assertEquals(entityNotFoundException.getMessage(), response.getMessage());

    }

}
