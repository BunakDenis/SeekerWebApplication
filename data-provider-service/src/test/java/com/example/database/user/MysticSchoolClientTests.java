package com.example.database.user;

import com.example.data.models.entity.mysticschool.ArticleCategory;
import com.example.database.DataProviderTestsBaseClass;
import com.example.database.api.client.MysticSchoolClient;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    public void testCheckUser() {

        //Given
        String email = "xisi926@ukr.net";

        //Then
        StepVerifier.create(mysticSchoolClient.checkUserAuthentication(email))
                .assertNext(resp -> {
                    log.debug("Response = {}", resp);
                    assertTrue(resp.isFound());
                    assertEquals(3, resp.getAccess_level());
                    assertTrue(resp.isActive());
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
                    assertEquals(0, resp.getAccess_level());
                    assertFalse(resp.isActive());
                })
                .verifyComplete();
    }

    @Test
    public void testGetArticleCategories() {

        ArticleCategory expectedDescipleWordArticle = getDescipleWordArticleCategory();
        ArticleCategory expectedMasterArticle = getMasterArticleCategory();
        ArticleCategory expectedMysticLiteratureArticle = getMysticLiteratureArticleCategory();

        StepVerifier.create(mysticSchoolClient.getArticleCategories())
                .assertNext(list -> {

                    ArticleCategory actualDescipleWordArticle = list.stream()
                            .filter(articleCategory -> articleCategory.getId() == expectedDescipleWordArticle.getId())
                            .findFirst().get();
                    ArticleCategory actualMasterArticle = list.stream()
                            .filter(articleCategory -> articleCategory.getId() == expectedMasterArticle.getId())
                            .findFirst().get();
                    ArticleCategory actualMysticLiteratureArticle = list.stream()
                            .filter(articleCategory -> articleCategory.getId() == expectedMysticLiteratureArticle.getId())
                            .findFirst().get();

                    assertFalse(list.isEmpty());
                    assertEquals(expectedDescipleWordArticle.getName(), actualDescipleWordArticle.getName());
                    assertEquals(expectedMasterArticle.getName(), actualMasterArticle.getName());
                    assertEquals(expectedMysticLiteratureArticle.getName(), actualMysticLiteratureArticle.getName());

                })
                .verifyComplete();

    }

    @Test
    public void testGetArticlesByDescipleWordArticleCategoryId() {

        ArticleCategory expectedDescipleWordArticleCategory = getDescipleWordArticleCategory();

        StepVerifier
                .create(mysticSchoolClient.getArticleByArticleCategoryId(expectedDescipleWordArticleCategory.getId()))
                .assertNext(list -> assertFalse(list.isEmpty()))
                .verifyComplete();
    }
    @Test
    public void testGetArticlesByMasterArticleCategoryId() {

        ArticleCategory expectedMasterArticleCategory = getMasterArticleCategory();

        StepVerifier
                .create(mysticSchoolClient.getArticleByArticleCategoryId(expectedMasterArticleCategory.getId()))
                .assertNext(list -> assertFalse(list.isEmpty()))
                .verifyComplete();
    }
    @Test
    public void testGetArticlesByMysticLiteratureArticleCategoryId() {

        ArticleCategory expectedMysticLiteratureArticleCategory = getMysticLiteratureArticleCategory();

        StepVerifier
                .create(mysticSchoolClient.getArticleByArticleCategoryId(expectedMysticLiteratureArticleCategory.getId()))
                .assertNext(list -> assertFalse(list.isEmpty()))
                .verifyComplete();
    }

    /*
            TODO
                1. Дописать тесты для получения статьи в рамках категории по ID категории
                2. Дописать тесты для получения книг
                3. Дописать тесты для получения практик
     */

    private ArticleCategory getDescipleWordArticleCategory() {
        return ArticleCategory.builder()
                .id(1)
                .name("Слово учеников")
                .build();
    }
    private ArticleCategory getMasterArticleCategory() {
        return ArticleCategory.builder()
                .id(2)
                .name("Статьи Мастера")
                .build();
    }
    private ArticleCategory getMysticLiteratureArticleCategory() {
        return ArticleCategory.builder()
                .id(3)
                .name("Учебная литература")
                .build();
    }

}
