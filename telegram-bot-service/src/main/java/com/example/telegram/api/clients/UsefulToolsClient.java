package com.example.telegram.api.clients;

import com.example.telegram.dto.FileServiceResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UsefulToolsClient {

    @Value("${API_MAIN_UTILS_SERVICE_URL}")
    private String baseUrl;

    @Value("${USEFUL_TOOLS_FILE_SERVICE_ENDPOINT}")
    private String usefulToolsFileServiceEndpoint;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl) // Устанавливаем базовый URL
                .build();
    }

    /**
     * Отправляет запрос на изменение расширения файла в main-utils-service.
     *
     * @param fileName Имя файла.
     * @param newExtension Новое расширение.
     * @return Mono с ответом от сервиса. Используйте .block() если не работаете в реактивном контексте.
     */
    public Mono<FileServiceResponse> changeFileExtension(String fileName, String newExtension) {
        // Определяем путь к эндпоинту
        String endpointPath = usefulToolsFileServiceEndpoint + "changeFileExtension";

        // Выполняем POST запрос
        return webClient.post()
                // Строим URI с query параметрами
                .uri(uriBuilder -> uriBuilder.path(endpointPath)
                        .queryParam("fileName", fileName)
                        .queryParam("newExtension", newExtension)
                        .build())
                // Указываем тип контента (хотя для query params это не всегда критично)
                .contentType(MediaType.APPLICATION_JSON) // Или другой, если ваш сервис ожидает
                // Указываем, что ожидаем JSON в ответ
                .accept(MediaType.APPLICATION_JSON)
                // Получаем ответ
                .retrieve()
                // Обработка ошибок HTTP статусов (опционально, но рекомендуется)
                // Например, если получили 4xx или 5xx
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        String.format("Error from MainUtilsService: Status %d, Body: %s",
                                                response.statusCode().value(), errorBody)))))
                // Преобразуем тело ответа в наш DTO
                .bodyToMono(FileServiceResponse.class);
    }


    public FileServiceResponse changeFileExtensionBlocking(String fileName, String newExtension) {
        return changeFileExtension(fileName, newExtension).block(); // Блокирует выполнение до получения ответа
    }
}