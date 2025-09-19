package com.example.database;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.service.JWTService;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.service.ModelMapperService;
import com.example.database.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
@EnableReactiveMethodSecurity
@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class DataProviderTests {

    @Value("${api.key.header.name}")
    private String apiKeyHeaderName;
    @Autowired
    private WebTestClient client;
    private static Dotenv dotenv;
    private static ObjectMapper objectMapper;
    private static ModelMapperService mapperService;
    private static PostgreSQLContainer<?> postgres;
    @Autowired
    private UserService userService;
    @MockBean
    private JWTService jwtService;

    static {
        postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

        postgres.start();

        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();

        mapperService = new ModelMapperService(new ModelMapper());
    }

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
