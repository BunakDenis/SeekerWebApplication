package com.example.database.user;


import com.example.data.models.entity.response.CheckUserResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.enums.UserRoles;
import com.example.data.models.exception.*;
import com.example.database.api.client.MysticSchoolClient;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.User;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.database.DataProviderTestsBaseClass;
import com.example.database.entity.UserDetails;
import com.example.database.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.example.data.models.consts.DataProviderEndpointsConsts.getApiUserEndpoint;
import static com.example.database.constants.UserConstantsForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UserDataControllerTests extends DataProviderTestsBaseClass {


    @Autowired
    private UserService userService;
    private static GenericContainer<?> torProxyContainer;
    private static String mysticSchoolBaseUrl;
    @Container
    static MockServerContainer mysticSchoolServerContainer =
            new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));
    static MockServerClient mysticSchoolClient;
    @MockBean
    private MysticSchoolClient mockMysticSchoolClient;

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

        mysticSchoolBaseUrl = dotenv.get("MYSTIC_SCHOOL_API_URL") + dotenv.get("MYSTIC_SCHOOL_API_VERSION");
    }


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {

        torProxyContainer.start();

        if (torProxyContainer.isRunning()) {

            mysticSchoolClient = new MockServerClient(
                    mysticSchoolServerContainer.getHost(),
                    mysticSchoolServerContainer.getServerPort()
            );

            log.debug("torProxyServerContainer host = {}", mysticSchoolServerContainer.getHost());
            log.debug("torProxyServerContainer port = {}", mysticSchoolServerContainer.getServerPort());

            registry.add("proxy.tor.host", () -> mysticSchoolServerContainer.getHost());
            registry.add("proxy.tor.port", () -> mysticSchoolServerContainer.getServerPort());
        }
    }


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
    void testSaveNullUser() throws JsonProcessingException {

        //Given
        ApiRequest<Object> request = new ApiRequest();

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse response = resp.getResponseBody();

                    String expectedMessage = response.getMessage();

                    EntityNullException entityNullException = assertThrows(EntityNullException.class, () -> {
                        userService.save(null);
                    });

                    String actualMessage = entityNullException.getMessage();

                    assertEquals(expectedMessage, actualMessage);

                });
    }
    @Test
    void testSaveUserWithEmptyUsername() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        expectedUser.setUsername("");

        UserDTO expectedUserDto = mapperService.toDTO(expectedUser, UserDTO.class);

        ApiRequest<Object> request = new ApiRequest(expectedUserDto);

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse<Object> response = resp.getResponseBody();

                    EntityEmptyFieldException entityEmptyFieldException = assertThrows(EntityEmptyFieldException.class, () -> {
                        userService.save(expectedUser);
                    });

                    String expectedResponseText = entityEmptyFieldException.getMessage();

                    assertEquals(expectedResponseText, response.getMessage());

                });
    }
    @Test
    void testSaveUserWithNullableUsername() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        expectedUser.setUsername(null);

        UserDTO expectedUserDto = mapperService.toDTO(expectedUser, UserDTO.class);

        ApiRequest<Object> request = new ApiRequest(expectedUserDto);

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse<Object> response = resp.getResponseBody();

                    EntityNullFieldException entityNullFieldException = assertThrows(EntityNullFieldException.class, () -> {
                        userService.save(expectedUser);
                    });

                    String expectedResponseText = entityNullFieldException.getMessage();

                    assertEquals(expectedResponseText, response.getMessage());

                });
    }
    @Test
    void testSaveUserWithEmptyPassword() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        expectedUser.setPassword("");

        UserDTO expectedUserDto = mapperService.toDTO(expectedUser, UserDTO.class);

        ApiRequest<Object> request = new ApiRequest(expectedUserDto);

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse<Object> response = resp.getResponseBody();

                    EntityEmptyFieldException entityEmptyFieldException = assertThrows(EntityEmptyFieldException.class, () -> {
                        userService.save(expectedUser);
                    });

                    String expectedResponseText = entityEmptyFieldException.getMessage();

                    assertEquals(expectedResponseText, response.getMessage());

                });
    }
    @Test
    void testSaveUserWithNullablePassword() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        expectedUser.setPassword(null);

        UserDTO expectedUserDto = mapperService.toDTO(expectedUser, UserDTO.class);

        ApiRequest<Object> request = new ApiRequest(expectedUserDto);

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse<Object> response = resp.getResponseBody();

                    EntityNullFieldException entityNullFieldException = assertThrows(EntityNullFieldException.class, () -> {
                        userService.save(expectedUser);
                    });

                    String expectedResponseText = entityNullFieldException.getMessage();

                    assertEquals(expectedResponseText, response.getMessage());

                });
    }
    @Test
    void testSaveUserWithEmptyEmail() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        expectedUser.setEmail("");

        UserDTO expectedUserDto = mapperService.toDTO(expectedUser, UserDTO.class);

        ApiRequest<Object> request = new ApiRequest(expectedUserDto);

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse<Object> response = resp.getResponseBody();

                    EntityEmptyFieldException entityEmptyFieldException = assertThrows(EntityEmptyFieldException.class, () -> {
                        userService.save(expectedUser);
                    });

                    String expectedResponseText = entityEmptyFieldException.getMessage();

                    assertEquals(expectedResponseText, response.getMessage());

                });
    }
    @Test
    void testSaveUserWithNullableEmail() throws JsonProcessingException {

        //Given
        User expectedUser = getUserForTests();
        expectedUser.setId(null);
        expectedUser.setEmail(null);

        UserDTO expectedUserDto = mapperService.toDTO(expectedUser, UserDTO.class);

        ApiRequest<Object> request = new ApiRequest(expectedUserDto);

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse<Object> response = resp.getResponseBody();

                    EntityNullFieldException entityNullFieldException = assertThrows(EntityNullFieldException.class, () -> {
                        userService.save(expectedUser);
                    });

                    String expectedResponseText = entityNullFieldException.getMessage();

                    assertEquals(expectedResponseText, response.getMessage());

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
                    client.post()
                            .uri(dataProviderEndpoint + getApiUserEndpoint("update/"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(apiKeyHeaderName, "apiHeader")
                            .bodyValue(userApiRequest)
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
                });
    }
    @Test
    void testGetUserById() {

        //Given
        long userId = 1L;

        //When
        client.get()
                .uri("/api/v1/user/id/" + userId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());
                });
    }
    @Test
    void testGetUserByUsername() {

        //Given
        String username = "tourist";

        //When
        client.get()
                .uri("/api/v1/user/username/" + username)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());

                    User actualUser = objectMapper.convertValue(apiResponse.getData(), User.class);

                    assertEquals(username, actualUser.getUsername());
                });
    }
    @Test
    void testGetUserByEmail() {

        //Given
        String email = "tourist@gmail.com";

        //When
        client.get()
                .uri("/api/v1/user/email/" + email)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());

                    User actualUser = objectMapper.convertValue(apiResponse.getData(), User.class);

                    assertEquals(email, actualUser.getEmail());
                });
    }
    @Test
    void testGetUserByTelegramUserId() {

        //Given
        long tgUserId = 55555L;

        //When
        client.get()
                .uri("/api/v1/user/telegram_user_id/" + tgUserId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());

                });
    }
    @Test
    void testGetUserByTelegramUserIdWithTelegramUser() {

        //Given
        long tgUserId = 55555L;

        //When
        client.get()
                .uri("/api/v1/user/telegram_user/telegram_user_id/" + tgUserId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());

                    User actualUser = objectMapper.convertValue(apiResponse.getData(), User.class);

                    TelegramUser actualTelegramUser =
                            mapperService.toEntity(
                                    apiResponse.getIncludedObject(
                                            ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()
                                    ),
                                    TelegramUser.class
                            );

                    actualUser.setTelegramUsers(List.of(actualTelegramUser));

                    assertEquals(tgUserId, actualUser.getTelegramUsers().get(0).getTelegramUserId());
                });
    }
    @Test
    void testGetUserByTelegramUserIdWithUserDetails() {

        //Given
        long tgUserId = 55555L;
        UserDetails expectedUserDetails = USER_DETAILS_FOR_TESTS;

        //When
        client.get()
                .uri("/api/v1/user/user_details/telegram_user_id/" + tgUserId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());

                    User actualUser = objectMapper.convertValue(apiResponse.getData(), User.class);

                    UserDetails actualUserDetails = objectMapper.convertValue(
                            apiResponse.getIncludedObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue()),
                            UserDetails.class
                    );

                    expectedUserDetails.setId(actualUserDetails.getId());
                    actualUser.setUserDetails(actualUserDetails);

                    assertEquals(expectedUserDetails.getFirstName(), actualUserDetails.getFirstName());
                    assertEquals(expectedUserDetails.getLastname(), actualUserDetails.getLastname());
                    assertEquals(expectedUserDetails.getBirthday(), actualUserDetails.getBirthday());
                    assertEquals(expectedUserDetails.getPhoneNumber(), actualUserDetails.getPhoneNumber());
                    assertEquals(expectedUserDetails.getGender(), actualUserDetails.getGender());
                    assertEquals(expectedUserDetails.getAvatarLink(), actualUserDetails.getAvatarLink());
                    assertEquals(expectedUserDetails.getLocation(), actualUserDetails.getLocation());
                    assertEquals(expectedUserDetails.getDateStartStudyingSchool(), actualUserDetails.getDateStartStudyingSchool());
                    assertEquals(expectedUserDetails.getCurator(), actualUserDetails.getCurator());
                });
    }
    @Test
    void testGetUserFullByTelegramUserId() {

        //Given
        long tgUserId = 55555L;
        UserDetails expectedUserDetails = USER_DETAILS_FOR_TESTS;
        TelegramUser expectedTelegramUser = TELEGRAM_USER_FOR_TESTS;

        //When
        client.get()
                .uri("/api/v1/user/full/telegram_user_id/" + tgUserId)
                .header(apiKeyHeaderName, "apiKey")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(resp -> {

                    ApiResponse apiResponse = resp.getResponseBody();

                    //Then
                    assertNotNull(apiResponse.getData());

                    User actualUser = objectMapper.convertValue(apiResponse.getData(), User.class);

                    UserDetails actualUserDetails = objectMapper.convertValue(
                            apiResponse.getIncludedObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue()),
                            UserDetails.class
                    );
                    TelegramUser actualTelegramUser = objectMapper.convertValue(
                            apiResponse.getIncludedObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                            TelegramUser.class
                    );

                    assertNotNull(actualUserDetails);
                    assertNotNull(actualTelegramUser);

                    actualUser.setUserDetails(actualUserDetails);

                    assertEquals(expectedUserDetails.getFirstName(), actualUserDetails.getFirstName());
                    assertEquals(expectedUserDetails.getLastname(), actualUserDetails.getLastname());
                    assertEquals(expectedUserDetails.getBirthday(), actualUserDetails.getBirthday());
                    assertEquals(expectedUserDetails.getPhoneNumber(), actualUserDetails.getPhoneNumber());
                    assertEquals(expectedUserDetails.getGender(), actualUserDetails.getGender());
                    assertEquals(expectedUserDetails.getAvatarLink(), actualUserDetails.getAvatarLink());
                    assertEquals(expectedUserDetails.getLocation(), actualUserDetails.getLocation());
                    assertEquals(expectedUserDetails.getDateStartStudyingSchool(), actualUserDetails.getDateStartStudyingSchool());
                    assertEquals(expectedUserDetails.getCurator(), actualUserDetails.getCurator());

                    assertEquals(expectedTelegramUser.getTelegramUserId(), actualTelegramUser.getTelegramUserId());
                    assertEquals(expectedTelegramUser.getUsername(), actualTelegramUser.getUsername());
                    assertEquals(expectedTelegramUser.isActive(), actualTelegramUser.isActive());

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
    @Test
    void testDeleteUserMethod() throws JsonProcessingException {

        ApiRequest apiRequest = getUserRequest();

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeaderName, "apiHeader")
                .bodyValue(objectMapper.writeValueAsString(apiRequest))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(apiResponse -> {

                    ApiResponse response = apiResponse.getResponseBody();

                    assertNotNull(response.getData());

                    UserDTO savedUserDto = objectMapper.convertValue(response.getData(), UserDTO.class);

                    client.post()
                            .uri(dataProviderEndpoint + getApiUserEndpoint("delete/" + savedUserDto.getId()))
                            .header(apiKeyHeaderName, "apiHeader")
                            .exchange()
                            .expectStatus().is2xxSuccessful()
                            .expectBody(ApiResponse.class)
                            .consumeWith(deleteResp -> {

                                ApiResponse deleteResponseBody = deleteResp.getResponseBody();

                                assertNotNull(deleteResponseBody.getData());

                                Boolean expectedDeleteAnswer = objectMapper.convertValue(
                                        deleteResponseBody.getData(),
                                        Boolean.class
                                );

                                assertTrue(expectedDeleteAnswer);
                            });
                });

    }
    @Test
    void testDeleteUserMethodWithInvalidId() throws JsonProcessingException {

        long userId = 500L;
        ApiRequest apiRequest = getUserRequest();

        //Then
        client.post()
                .uri(dataProviderEndpoint + getApiUserEndpoint("delete/" + userId))
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(deleteResp -> {

                    ApiResponse deleteResponseBody = deleteResp.getResponseBody();

                    assertNotNull(deleteResponseBody.getData());

                    EntityNotFoundException entityNotFoundException = assertThrows(
                            EntityNotFoundException.class, () ->
                                    userService.getUserById(userId)
                    );

                    String expectedExceptionMsg = entityNotFoundException.getMessage();
                    String actualExceptionMsg = deleteResponseBody.getMessage();

                    Boolean expectedDeleteAnswer = objectMapper.convertValue(
                            deleteResponseBody.getData(),
                            Boolean.class
                    );

                    assertFalse(expectedDeleteAnswer);
                    assertEquals(expectedExceptionMsg, actualExceptionMsg);
                });
    }
    @Test
    void testCheckUserAuthInMysticSchoolBdByTelegramUserId() throws JsonProcessingException {

        //Given
        Long id = TELEGRAM_USER_FOR_TESTS.getTelegramUserId();
        CheckUserResponse expectedCheckUserResponse = CheckUserResponse.builder()
                .found(true)
                .active(true)
                .access_level((byte) 2)
                .build();

        ApiRequest<CheckUserResponse> apiRequest = new ApiRequest(expectedCheckUserResponse);

        //When
        mysticSchoolClient.when(
                request()
                        .withMethod("GET")
                        .withPath(getApiUserEndpoint("check/auth/" + id)))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(apiRequest))
        );



        Mockito.when(
                mockMysticSchoolClient.checkUserAuthentication(any())
        ).thenReturn(Mono.just(ApiResponse.<CheckUserResponse>builder()
                .data(expectedCheckUserResponse)
                .build()
        ));

        //Then
        client.get()
                .uri(dataProviderEndpoint + getApiUserEndpoint("check/auth/" + id))
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(apiResp -> {

                    ApiResponse responseBody = apiResp.getResponseBody();

                    log.debug("Response body = {}", responseBody);

                    assertNotNull(responseBody.getData());

                    CheckUserResponse checkUserResponse = objectMapper.convertValue(
                            responseBody.getData(), CheckUserResponse.class
                    );

                    assertTrue(checkUserResponse.isFound());
                    assertEquals(checkUserResponse.getAccess_level(), expectedCheckUserResponse.getAccess_level());
                    assertTrue(checkUserResponse.isActive());

                });
    }
    @Test
    void testCheckUserAuthInMysticSchoolBdByTelegramUserIdWithNotAuthUser() throws JsonProcessingException {

        //Given
        Long id = 55555L;
        CheckUserResponse expectedCheckUserResponse = CheckUserResponse.builder()
                .found(false)
                .build();

        ApiRequest<CheckUserResponse> apiRequest = new ApiRequest(expectedCheckUserResponse);

        //When
        mysticSchoolClient.when(
                        request()
                                .withMethod("GET")
                                .withPath(getApiUserEndpoint("check/auth/" + id)))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(apiRequest))
                );

        Mockito.when(
                mockMysticSchoolClient.checkUserAuthentication(any())
        ).thenReturn(Mono.just(
                ApiResponse.<CheckUserResponse>builder()
                        .data(expectedCheckUserResponse)
                        .build()
        ));

        //Then
        client.get()
                .uri(dataProviderEndpoint + getApiUserEndpoint("check/auth/" + id))
                .header(apiKeyHeaderName, "apiHeader")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ApiResponse.class)
                .consumeWith(apiResp -> {

                    ApiResponse responseBody = apiResp.getResponseBody();

                    log.debug("Response body = {}", responseBody);

                    assertNotNull(responseBody.getData());

                    CheckUserResponse checkUserResponse = objectMapper.convertValue(
                            responseBody.getData(), CheckUserResponse.class
                    );

                    assertFalse(checkUserResponse.isFound());
                });
    }
    private ApiRequest getUserRequest() {

        User vasya = User.builder()
                .username("vasya")
                .password("12345")
                .role(UserRoles.USER.getRole())
                .email("vs@gmail.com")
                .isActive(true)
                .build();

        UserDTO userDTO = mapperService.toDTO(vasya, UserDTO.class);

        return new ApiRequest(userDTO);
    }

}
