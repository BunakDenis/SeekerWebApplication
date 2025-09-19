package com.example.database.user;


import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.DataProviderTestsBaseClass;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;



@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UserDataControllerTests extends DataProviderTestsBaseClass {


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
