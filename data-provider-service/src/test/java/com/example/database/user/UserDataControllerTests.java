package com.example.database.user;


import com.example.data.models.entity.User;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.DataProviderTestsBaseClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.data.models.consts.DataProviderEndpointsConsts.getApiUserEndpoint;
import static com.example.data.models.consts.TelegramBotConstantsForTests.*;
import static com.example.data.models.consts.ResponseMessageProvider.*;
import static com.example.data.models.consts.DataProviderEndpointsConsts.*;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UserDataControllerTests extends DataProviderTestsBaseClass {


    @Test
    void testSaveUser() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        ApiRequest request = new ApiRequest(mapperService.toDTO(expectedUser, UserDTO.class));

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse response = resp.getResponseBody();

                    assertNotNull(response.getData());

                    UserDTO actualUser = objectMapper.convertValue(response.getData(), UserDTO.class);

                    assertEquals(expectedUser.getUsername(), actualUser.getUsername());
                    assertEquals(expectedUser.getPassword(), actualUser.getPassword());
                    assertEquals(expectedUser.getEmail(), actualUser.getEmail());
                    assertEquals(expectedUser.getRole(), actualUser.getRole());

                });

    }
    @Test
    void testUpdateUser() throws JsonProcessingException {

        //Given
        client.get()
                .uri(dataProviderEndpoint + getApiUserEndpoint("id/1"))
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse response = resp.getResponseBody();
                    log.debug("Response = {}", resp);
                    UserDTO expectedUser = objectMapper.convertValue(response.getData(), UserDTO.class);
                    expectedUser.setUsername("master");
                    expectedUser.setEmail("master@mystic.com");

                    ApiRequest<UserDTO> userApiRequest = new ApiRequest<>(expectedUser);

                    //Then
                    try {
                        client.post()
                                .uri(dataProviderEndpoint + getApiUserEndpoint("update/"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(apiKeyHeaderName, "apiHeader")
                                .bodyValue(objectMapper.writeValueAsString(userApiRequest))
                                .exchange()
                                .expectBody(ApiResponse.class)
                                .consumeWith(saveResp -> {

                                    ApiResponse savedResponse = saveResp.getResponseBody();

                                    assertNotNull(savedResponse.getData());

                                    UserDTO actualUser = objectMapper.convertValue(
                                            savedResponse.getData(), UserDTO.class
                                    );

                                    assertEquals(expectedUser.getUsername(), actualUser.getUsername());
                                    assertEquals(expectedUser.getPassword(), actualUser.getPassword());
                                    assertEquals(expectedUser.getEmail(), actualUser.getEmail());
                                    assertEquals(expectedUser.getRole(), actualUser.getRole());

                                });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    @Test
    void testGetUserWithInvalidId() {

        //Given
        long userId = 500L;

        //When
        client.get()
                .uri("/api/v1/user/id/" + userId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {
                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
                        userService.getUserById(userId);
                    });

                    assertEquals(entityNotFoundException.getMessage(), apiResponse.getMessage());
                });



    }

}
