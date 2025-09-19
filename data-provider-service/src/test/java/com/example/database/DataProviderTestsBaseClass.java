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


import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
@EnableReactiveMethodSecurity
@Slf4j
public abstract class DataProviderTestsBaseClass {

    @Value("${api.key.header.name}")
    protected String apiKeyHeaderName;
    @Autowired
    protected WebTestClient client;
    protected static ObjectMapper objectMapper;
    protected static ModelMapperService mapperService;
    protected static PostgreSQLContainer<?> postgres;
    @Autowired
    protected UserService userService;
    @MockBean
    protected JWTService jwtService;

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

}
