package com.example.database;


import com.example.data.models.service.JWTService;
import com.example.database.service.ModelMapperService;
import com.example.database.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
@EnableReactiveMethodSecurity
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

}
