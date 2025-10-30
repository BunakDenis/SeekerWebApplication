package com.example.database.api.controller;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.CuratorDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.DataProviderTestsBaseClass;
import com.example.database.entity.Curator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CuratorDataControllerTest extends DataProviderTestsBaseClass {


    @Test
    @Order(1)
    void saveNewCuratorShouldPersistAndReturnCreatedCurator() throws Exception {

        // Given
        CuratorDTO incomingDto = CuratorDTO.builder()
                .id(10L)
                .build();


        ApiRequest<CuratorDTO> request = new ApiRequest<>(incomingDto);

        // When / Then
        client.post()
                .uri(dataProviderEndpoint + "/curator/save")
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse response = resp.getResponseBody();
                    int status = resp.getStatus().value();

                    Assertions.assertNotNull(response.getData());

                    Assertions.assertEquals(HttpStatus.OK.value(), status);
                    Assertions.assertEquals(ResponseMessageProvider.SUCCESSES_MSG, response.getMessage());
                });
    }

    @Order(2)
    @Test
    void getById() {

        // Given
        long expectedId = 55L;


        // When / Then
        client.get()
                .uri(dataProviderEndpoint + "/curator/" + expectedId)
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse apiResponse = resp.getResponseBody();
                    Curator actual = objectMapper.convertValue(apiResponse.getData(), Curator.class);

                    Assertions.assertNotNull(apiResponse.getData());

                    Assertions.assertEquals(expectedId, actual.getId());
                });
    }

    @Order(3)
    @Test
    void getByIdWhenNotExistsShouldReturnNotFoundError() {

        // Given
        long notExistingId = 999L;


        // When / Then
        client.get()
                .uri(dataProviderEndpoint + "/curator/" + notExistingId)
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse apiResponse = resp.getResponseBody();

                    EntityNotFoundException ex =
                            Assertions.assertThrows(
                                    EntityNotFoundException.class,
                                    () -> curatorService.getById(notExistingId)
                            );

                    Assertions.assertEquals(ex.getMessage(), apiResponse.getMessage());
                });
    }

    @Order(4)
    @Test
    void existsById() {

        // Given
        long queriedId = 55L;

        // When / Then
        client.get()
                .uri(dataProviderEndpoint + "/curator/exists/" + queriedId)
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse apiResponse = resp.getResponseBody();

                    Assertions.assertNotNull(apiResponse.getData());

                    Boolean exists = objectMapper.convertValue(apiResponse.getData(), Boolean.class);

                    Assertions.assertTrue(exists);
                    Assertions.assertEquals(ResponseMessageProvider.SUCCESSES_MSG, apiResponse.getMessage());
                });
    }

    @Order(5)
    @Test
    void existsByIdShouldReturnFalseWhenNoCurator() {

        // Given
        long queriedId = 333L;

        // When / Then
        client.get()
                .uri(dataProviderEndpoint + "/curator/exists/" + queriedId)
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse apiResponse = resp.getResponseBody();

                    Assertions.assertNotNull(apiResponse.getData());

                    Boolean exists = objectMapper.convertValue(apiResponse.getData(), Boolean.class);

                    Assertions.assertFalse(exists);
                    Assertions.assertEquals(ResponseMessageProvider.SUCCESSES_MSG, apiResponse.getMessage());
                });
    }

    @Order(6)
    @Test
    void delete() throws JsonProcessingException {

        // Given
        CuratorDTO incomingDto = CuratorDTO.builder()
                .id(15L)
                .build();


        ApiRequest<CuratorDTO> request = new ApiRequest<>(incomingDto);

        // When
        client.post()
                .uri(dataProviderEndpoint + "/curator/save")
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class);

        // Then
        client.post()
                .uri(dataProviderEndpoint + "/curator/delete/" + incomingDto.getId())
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(deleteResp -> {
                    ApiResponse deleteResponse = deleteResp.getResponseBody();

                    Assertions.assertNotNull(deleteResponse.getData());

                    Boolean deleted = objectMapper.convertValue(deleteResponse.getData(), Boolean.class);
                    Assertions.assertTrue(deleted);
                });
    }

}
