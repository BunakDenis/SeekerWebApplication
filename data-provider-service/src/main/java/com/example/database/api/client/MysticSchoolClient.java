package com.example.database.api.client;


import com.example.data.models.entity.mysticschool.ArticleCategory;
import com.example.data.models.entity.response.CheckUserResponse;
import com.example.data.models.exception.ApiException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.NoopAddressResolverGroup;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;
import reactor.util.retry.Retry;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class MysticSchoolClient {

    @Value("${mystic.school.api.url}")
    private String apiUrl;

    @Value("${mystic.school.api.version}")
    private String apiVersion;

    @Value("${mystic.school.api.key}")
    private String apiKey;

    @Value("${proxy.tor.enabled:true}")
    private boolean proxyEnabled;

    @Value("${proxy.tor.host:localhost}")
    private String proxyHost;

    @Value("${proxy.tor.port:9050}")
    private int proxyPort;

    @Value("${mystic.school.check.user.endpoint}")
    private String checkUserEndpoint;

    @Value("${mystic.school.article.endpoint}")
    private String articlesEndpoint;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        String baseUrl = apiUrl + apiVersion;
        log.debug("Устанавливаем baseUrl для MysticSchoolClient = {}", baseUrl);

        ConnectionProvider provider = ConnectionProvider.builder("mystic-school-provider")
                .maxConnections(200)
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                // чтобы DNS делал прокси (SOCKS5), а не локалка — не резолвим имена заранее
                .resolver(NoopAddressResolverGroup.INSTANCE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20_000)
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60))
                        .addHandlerLast(new WriteTimeoutHandler(60)))
                .followRedirect(false)
                .compress(true)
                .doOnRequest((req, conn) -> log.debug("Outgoing request: {}", req.method().name() + " " + req.uri())) // Log outgoing requests
                .doOnResponse((resp, conn) -> log.debug("Received response: {} from {}", resp.status(), conn.address()));

        log.debug("proxyEnabled = " + proxyEnabled);

        if (proxyEnabled) {
            httpClient = httpClient.proxy(spec -> spec
                    .type(ProxyProvider.Proxy.SOCKS5)
                    .address(new InetSocketAddress(proxyHost, proxyPort))// Directly use InetSocketAddress
                    .connectTimeoutMillis(60000)
            );
        }

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .filter(ExchangeFilterFunction.ofRequestProcessor(req -> {
                    log.debug("Request URL: {}", req.url());
                    return Mono.just(req);
                }))
                .build();
    }

    public Mono<CheckUserResponse> checkUserAuthentication(String email) {

        log.debug("Проверка пользователя в Mystic School API, email={}", email);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(checkUserEndpoint).queryParam("email", email).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String msg = String.format("Mystic API returned %d: %s",
                                            response.statusCode().value(), body);
                                    return Mono.error(new ApiException(msg));
                                }))
                .bodyToMono(CheckUserResponse.class)
                .timeout(Duration.ofSeconds(30))
                // небольшой retry на кратковременные сетевые ошибки (настройте по потребности)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(500)).filter(throwable -> {
                    log.warn("Retrying due to {}", throwable.toString());
                    return true;
                }))
                .doOnError(e -> log.error("Ошибка при вызове MysticSchool API: {}", e.toString()))
                .onErrorMap(e -> new ApiException("Failed to call Mystic School API"));
    }
    public Mono<List<ArticleCategory>> getArticleCategories() {

        log.debug("Запрос Mystic School API на получение категорий статей.");

        return webClient.get()
                .uri(articlesEndpoint)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String msg = String.format("Mystic API returned %d: %s",
                                            response.statusCode().value(), body);
                                    return Mono.error(new ApiException(msg));
                                }))
                .bodyToFlux(ArticleCategory.class) // Use bodyToFlux to get a Flux
                .collectList() // Collect the Flux into a List
                .timeout(Duration.ofSeconds(30))
                // небольшой retry на кратковременные сетевые ошибки (настройте по потребности)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(500)).filter(throwable -> {
                    log.warn("Retrying due to {}", throwable.toString());
                    return true;
                }))
                .doOnError(e -> log.error("Ошибка при вызове MysticSchool API: {}", e.toString()))
                .onErrorMap(e -> new ApiException("Failed to call Mystic School API"));

    }
    public Mono<List<ArticleCategory>> getArticleByArticleCategoryId(int id) {

        log.debug("Запрос Mystic School API на получение всех статей категории id = " + id);

        return webClient.get()
                .uri(builder -> builder.path(articlesEndpoint).queryParam("id", id).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String msg = String.format("Mystic API returned %d: %s",
                                            response.statusCode().value(), body);
                                    return Mono.error(new ApiException(msg));
                                }))
                .bodyToFlux(ArticleCategory.class)
                .collectList()
                .timeout(Duration.ofSeconds(30))
                // небольшой retry на кратковременные сетевые ошибки (настройте по потребности)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(500)).filter(throwable -> {
                    log.warn("Retrying due to {}", throwable.toString());
                    return true;
                }))
                .doOnError(e -> log.error("Ошибка при вызове MysticSchool API: {}", e.toString()))
                .onErrorMap(e -> new ApiException("Failed to call Mystic School API"));

    }

}

