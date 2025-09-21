package com.example.database.user;

import com.example.data.models.entity.dto.response.CheckUserResponse;
import com.example.database.DataProviderTestsBaseClass;
import com.example.database.api.client.MysticSchoolClient;
import com.example.database.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static com.example.data.models.consts.DataProviderEndpointsConsts.*;


@Slf4j
public class MysticSchoolClientTests extends DataProviderTestsBaseClass {


    @Autowired
    private MysticSchoolClient mysticSchoolClient;
    private static GenericContainer<?> torProxyContainer;

    static {

        // Загружаем .env ещё до запуска Spring
        Dotenv dotenv = Dotenv.configure()
                .directory("../.env")
                .ignoreIfMissing()
                .load();

        String portStr = System.getenv("PROXY_TOR_PORT");
        if (portStr == null) {
            portStr = dotenv.get("PROXY_TOR_PORT", "9050");
        }

        int torPort = Integer.parseInt(portStr);

        torProxyContainer = new GenericContainer<>(DockerImageName.parse("dperson/torproxy:latest"))
                .withExposedPorts(torPort);
    }

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry) {

        torProxyContainer.start();

        if (torProxyContainer.isRunning()) {

            String host = torProxyContainer.getHost();
            Integer mappedPort = torProxyContainer.getMappedPort(
                    torProxyContainer.getExposedPorts().get(0)
            );

            log.debug("torProxyContainer host = {}", host);
            log.debug("torProxyContainer mapped port = {}", mappedPort);

            registry.add("proxy.tor.host", () -> host);
            registry.add("proxy.tor.port", () -> mappedPort);
        }
    }

    @Test
    public void testIsRunningTorProxyContainer() {
        assertTrue(torProxyContainer.isRunning());
    }
/*
    @Test
    public void testCheckUser() {

        //Given
        String email = "xisi926@ukr.net";

        //Then
        StepVerifier.create(mysticSchoolClient.checkUserAuthentication(email))
                .assertNext(resp -> {
                    log.debug("Response = {}", resp);
                    assertTrue(resp.isFound());
                })
                .verifyComplete();
    }

    @Test
    public void testCheckUserWithInvalidEmail() {

        //Given
        String email = "test@gmail.com";

        //Then
        StepVerifier.create(mysticSchoolClient.checkUserAuthentication(email))
                .assertNext(resp -> {
                    log.debug("Response = {}", resp);
                    assertFalse(resp.isFound());
                })
                .verifyComplete();
    }

    @Test
    public void testGetArticleCategories() {

        StepVerifier.create(mysticSchoolClient.getArticleCategories())
                .assertNext(list -> {

                    list.forEach(articleCategory -> log.debug("Категория статьи = {}", articleCategory));

                    assertFalse(list.isEmpty());

                })
                .verifyComplete();

    }
*/
}
