package com.example.database;


import com.example.data.models.service.JWTService;
import com.example.database.exception.RestControllerExceptionHandler;
import com.example.database.service.ModelMapperService;
import com.example.database.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


import java.io.File;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
@EnableReactiveMethodSecurity
@Slf4j
public abstract class DataProviderTestsBaseClass {


    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Value("${api.key.header.name}")
    protected String apiKeyHeaderName;
    @Value("${telegram.data.api.version}")
    protected String dataProviderEndpoint;
    @Autowired
    protected WebTestClient client;
    @Autowired
    protected static ObjectMapper objectMapper;
    @Autowired
    protected ModelMapperService mapperService;
    @Autowired
    protected UserService userService;
    @MockBean
    protected JWTService jwtService;

    @BeforeEach
    public void mockMethodCalls() {
        Mockito.when(jwtService.extractUsername(any())).thenReturn("telegram-bot-service");
    }

}
