package com.example.telegram;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.*;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.telegram.*;
import com.example.data.models.entity.jwt.JwtDataProvideDataImpl;
import com.example.data.models.entity.jwt.JwtTelegramDataImpl;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.enums.JWTDataSubjectKeys;
import com.example.data.models.enums.UserRoles;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.UiElements;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.keyboard.ReplyKeyboardMarkupFactory;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.utils.datetime.DateTimeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.mock.Expectation;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.example.data.models.consts.ResponseMessageProvider.*;
import static com.example.data.models.enums.ResponseIncludeDataKeys.*;
import static com.example.telegram.constanst.TelegramBotConstantsForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class TelegramBotTests extends TelegramTestsBaseClass {


    @Value("${default.utc.zone.id}")
    private String zoneId;
    @Value("${persistent.auth.expiration.time}")
    private long persistentSessionExpirationTime;
    @Value("${transient.auth.expiration.time}")
    private long transientSessionExpirationTime;
    private Map<String, ApiResponse> responses;
    @Container
    static MockServerContainer mockServerContainer =
            new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));
    static MockServerClient mockServerClient;


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mockServerClient = new MockServerClient(
                mockServerContainer.getHost(),
                mockServerContainer.getServerPort()
        );

        log.debug("MockServerContainer endpoint = {}", mockServerContainer.getEndpoint());

        registry.add("data.provide.api.url", mockServerContainer::getEndpoint);
    }
    @BeforeEach
    public void initVariables() {
        responses = new LinkedHashMap<>();

        ApiResponse<UserDTO> userDTOApiResponse = ApiResponse.<UserDTO>builder()
                .status(HttpStatus.OK)
                .message(ResponseMessageProvider.SUCCESSES_MSG)
                .data(mapperService.toDTO(getUserForTests(), UserDTO.class))
                .build();

        ApiResponse<TelegramChatDTO> telegramChatResponse = ApiResponse.<TelegramChatDTO>builder()
                .status(HttpStatus.OK)
                .message(ResponseMessageProvider.SUCCESSES_MSG)
                .data(mapperService.toDTO(telegramChatForTests, TelegramChatDTO.class))
                .includeObject(TELEGRAM_USER.getKeyValue(), TELEGRAM_USER_FOR_TESTS)
                .build();

        VerificationCode verificationCodeForTests = VERIFICATION_CODE_FOR_TESTS;
        verificationCodeForTests.setCreatedAt(LocalDateTime.now(ZoneId.of(zoneId)));
        verificationCodeForTests.setExpiresAt(LocalDateTime.now(ZoneId.of(zoneId)).plusMinutes(30L));

        ApiResponse<VerificationCodeDTO> verificationCodeResponse = ApiResponse.<VerificationCodeDTO>builder()
        .status(HttpStatus.OK)
                .message(ResponseMessageProvider.SUCCESSES_MSG)
                .data(mapperService.toDTO(verificationCodeForTests, VerificationCodeDTO.class))
                .build();

        telegramChatResponse.addIncludeObject("telegram_user", TELEGRAM_USER_FOR_TESTS);

        verificationCodeResponse.addIncludeObject("user", mapperService.toDTO(getUserForTests(), UserDTO.class));

        responses.put(USER.getKeyValue(), userDTOApiResponse);
        responses.put(TELEGRAM_CHAT.getKeyValue(), telegramChatResponse);
        responses.put(VERIFICATION_CODE.getKeyValue(), verificationCodeResponse);
    }
    @AfterEach
    public void afterEach() {
        mockServerClient.reset();
    }
    @Test
    @Order(1)
    public void testUnknownCommand() throws JsonProcessingException {
        //Given
        Update update = createTelegramUpdate("fff");

        String expectedMsgText = MessageProvider.UNKNOWN_COMMAND_OR_QUERY;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        //When
        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        //printToConsoleRegisteredMockRequests();

        WebTestClient.ResponseSpec exchange = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchange.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        //Then
        assertEquals(expectedMsgText, actual.getText());
    }
    @Test
    @Order(2)
    public void testStartCommand() throws Exception {

        log.debug("testStartCommand");

        //Given
        UserRoles userRole = UserRoles.valueOf(getUserForTests().getRole().toUpperCase());
        List<KeyboardRow> expectedKeyboard =
                ReplyKeyboardMarkupFactory.getMainMenuKeyboard(userRole);
        KeyboardRow navigationControlKeyBoard = ReplyKeyboardMarkupFactory.getNavigationControlKeyBoard();

        expectedKeyboard.add(0, navigationControlKeyBoard);

        Update update = createTelegramUpdate(Commands.START.getCommand());
        Message message = update.getMessage();

        String expectedMsgText = message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + "! " +
                MessageProvider.START_MSG;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        //When
        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        WebTestClient.ResponseSpec exchange = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchange.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        assertTrue(actual.getReplyMarkup() instanceof ReplyKeyboardMarkup);

        ReplyKeyboardMarkup actualMarkup = (ReplyKeyboardMarkup) actual.getReplyMarkup();
        List<KeyboardRow> actualMenuKeyBoards = actualMarkup.getKeyboard();

        //Then
        for (int i = 0; i < actualMenuKeyBoards.size(); i++) {
            assertEquals(expectedKeyboard.get(i), actualMenuKeyBoards.get(i));
        }

        assertEquals(Long.toString(message.getChatId()), actual.getChatId());
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(8)
    public void testStartCommandWithExpiredTransientToken() throws JsonProcessingException {

        log.debug("Тесты долгосрочной сессии с истёкшим сроком");

        //Given
        String expectedMsg = WarnMessageProvider.RE_AUTHORIZATION_MSG;

        Update update = createTelegramUpdate("/start");

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        //When
        telegramUserAuthFilterMockRequests();

        mockTelegramChatGetByIdRequest(telegramChatResponse.getData().getTelegramChatId(), telegramChatResponse);

        JwtTelegramDataImpl jwtDataForPersistentToken = JwtTelegramDataImpl.builder()
                .userDetails(USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS)
                .expirationTime(0)
                .subjects(
                        Map.of(
                                JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(),
                                TELEGRAM_USER_FOR_TESTS.getTelegramUserId()
                        )
                )
                .build();

        JwtTelegramDataImpl jwtDataForTransientToken = JwtTelegramDataImpl.builder()
                .userDetails(USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS)
                .expirationTime(0)
                .subjects(
                        Map.of(
                                JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(),
                                TELEGRAM_USER_FOR_TESTS.getTelegramUserId()
                        )
                )
                .build();

        String persistentToken = jwtService.generateToken(jwtDataForPersistentToken);
        String transientToken = jwtService.generateToken(jwtDataForTransientToken);

        PersistentSession persistentSession = PersistentSession.builder()
                .id(45678L)
                .data(persistentToken)
                .isActive(true)
                .telegramSession(TELEGRAM_SESSION_FOR_TESTS)
                .build();

        TransientSession transientSession = TransientSession.builder()
                .id(56789L)
                .data(transientToken)
                .isActive(true)
                .telegramSession(TELEGRAM_SESSION_FOR_TESTS)
                .build();

        TelegramSessionDTO telegramSessionDTO =
                mapperService.toDTO(TELEGRAM_SESSION_FOR_TESTS, TelegramSessionDTO.class);
        PersistentSessionDTO persistentSessionDTO = mapperService.toDTO(persistentSession, PersistentSessionDTO.class);
        TransientSessionDTO transientSessionDTO = mapperService.toDTO(transientSession, TransientSessionDTO.class);


        ApiResponse<TelegramSessionDTO> telegramSessionResponse = ApiResponse.<TelegramSessionDTO>builder()
                .data(telegramSessionDTO)
                .includeObject(TELEGRAM_USER.getKeyValue(), TELEGRAM_USER_FOR_TESTS)
                .includeList(PERSISTENT_SESSION.getKeyValue(), List.of(persistentSessionDTO))
                .includeList(TRANSIENT_SESSION.getKeyValue(), List.of(transientSessionDTO))
                .build();

        ApiResponse<PersistentSessionDTO> persistentSessionResponse = ApiResponse.<PersistentSessionDTO>builder()
                .data(persistentSessionDTO)
                .build();

        mockTelegramSessionByTelegramUserIdGetRequest(TELEGRAM_API_USER_FOR_TESTS.getId(), telegramSessionResponse);

        mockPersistentSessionAddRequest(persistentSessionResponse);

        TelegramUser telegramUserForTests = TELEGRAM_USER_FOR_TESTS;
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(telegramUserForTests, TelegramUserDTO.class);

        TelegramSession telegramSessionForTests = TELEGRAM_SESSION_FOR_TESTS;
        telegramSessionForTests.setLastAuthWarnTimestamp(LocalDateTime.now(ZoneId.of(zoneId)).minusHours(2L));
        TelegramSessionDTO telegramSessionIncludeDto = mapperService.toDTO(telegramSessionForTests, TelegramSessionDTO.class);

        ApiResponse<TelegramUserDTO> telegramUserWithTelegramSessionResponse = ApiResponse.<TelegramUserDTO>builder()
                .data(telegramUserDTO)
                .includeObject(
                        TELEGRAM_SESSION.getKeyValue(),
                        telegramSessionIncludeDto
                        )
                .build();

        mockTelegramUserWithTelegramSessionGetRequest(
                telegramUserForTests.getTelegramUserId(),
                telegramUserWithTelegramSessionResponse
        );

        ApiResponse<TelegramSession> telegramSessionSaveResponse = ApiResponseUtilsService.success(telegramSessionForTests);
        telegramSessionSaveResponse.addIncludeObject(TELEGRAM_USER.getKeyValue(), telegramUserDTO);
        mockTelegramSessionSaveRequest(telegramSessionSaveResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        ArgumentCaptor<Long> chatIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> messageTextCaptor = ArgumentCaptor.forClass(String.class);


        Mockito.verify(telegramBotMessageSender)
                .sendMessage(chatIdCaptor.capture(), messageTextCaptor.capture());

        Mockito.verify(telegramBotMessageSender)
                .sendMessage(sendMessageCaptor.capture());

        String actualMessage = messageTextCaptor.getValue();

        //Then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.validateToken(persistentToken, USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS);
        });
        assertEquals(expectedMsg, actualMessage);

    }
/*
    @Test
    public void testStartCommandFromNotRegisteredUser() throws JsonProcessingException {

        log.debug("Тесты команды старт для незарегистрированного юзера");

        //Given
        String expectedMsg = MessageProvider.START_MSG;
        Update update = createTelegramUpdate(Commands.START.getCommand());
        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<?> telegramChatResponse = ApiResponse.builder()
                .message("Telegram chat with telegram user id=" + TELEGRAM_API_USER_FOR_TESTS.getId() + ", not found")
                .build();

        //When
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user_id/" + TELEGRAM_USER_FOR_TESTS.getTelegramUserId())
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse))
                );

        telegramChatResponse.setMessage("Telegram chat not found");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/" + TELEGRAM_API_CHAT_FOR_TESTS.getId())
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse))
                );

        ApiResponse<?> userApiResponse = ApiResponse.builder()
                .message("User with telegram user id=" + TELEGRAM_API_USER_FOR_TESTS.getId() + ", not found")
                .build();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/telegram_user_id/" + TELEGRAM_API_USER_FOR_TESTS.getId())
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(userApiResponse))
                );

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/username/" + getUserForTests().getUsername())
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(userApiResponse))
                );

        WebTestClient.ResponseSpec exchangeStartCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeStartCommand.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        log.debug("Sant message {}", actual);

    }
*/
    @Test
    @Order(3)
    public void testAuthCommand() throws JsonProcessingException {

        log.debug("testAuthCommand()");

        //Given
        Update update = createTelegramUpdate(Commands.AUTHORIZE.getCommand());

        String expectedMsgText = MessageProvider.EMAIL_CHECKING_MSG;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());

        telegramChatResponse.setData(telegramChatDTO);

        //When
        telegramUserAuthFilterMockRequests();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockTelegramChatAddRequest(telegramChatResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        Mockito.verify(telegramBotMessageSender,Mockito.timeout(5000L)).sendMessage(captor.capture());

        SendMessage actual = captor.getValue();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(3)
    public void testValidInputEmailChatStateInAuthCommand() throws JsonProcessingException {
        //Given
        String testMsg = "test@seeker.com";
        Update update = createTelegramUpdate(testMsg);

        String expectedMsgText = MessageProvider.getEmailVerificationMsg(testMsg);

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.ENTER_EMAIL.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        ApiResponse<VerificationCodeDTO> verificationCodeResponse = responses.get(VERIFICATION_CODE.getKeyValue());

        //When
        telegramUserAuthFilterMockRequests();

        Mockito.when(emailService.isEmailAddressValid(testMsg)).thenReturn(true);

        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/otp_code/add/"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(verificationCodeResponse)));

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        ApiResponse checkUserApiResponse = ApiResponse.builder()
                .data(CHECK_USER_RESPONSE)
                .build();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/check/auth/"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(objectMapper.writeValueAsString(checkUserApiResponse))
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON));

        ApiResponse userResponse = responses.get(USER.getKeyValue());

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/email/.*"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(objectMapper.writeValueAsString(userResponse))
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON));

        mockTelegramChatAddRequest(telegramChatResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        mockServerClient.verify(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/otp_code/add/"),
                VerificationTimes.once()
        );

        ArgumentCaptor<String> captorToSentEmail = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorSubjectSentEmail = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorTextSentEmail = ArgumentCaptor.forClass(String.class);

        Mockito.verify(emailService)
                .sendSimpleMail(
                        captorToSentEmail.capture(),
                        captorSubjectSentEmail.capture(),
                        captorTextSentEmail.capture()
                );

        SendMessage actual = captorSendMessage();

        //Then
        assertEquals(captorToSentEmail.getValue(), testMsg);
        assertFalse(captorSubjectSentEmail.getValue().isEmpty());
        assertFalse(captorTextSentEmail.getValue().isEmpty());
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(4)
    public void testNotValidInputEmailChatStateInAuthCommand() throws JsonProcessingException {
        //Given
        String testMsg = "test";
        Update update = createTelegramUpdate(testMsg);

        String expectedMsgText = WarnMessageProvider.getNotValidEmailAddress(testMsg, Commands.REGISTER.getCommand());

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.ENTER_EMAIL.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        //When
        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockTelegramChatAddRequest(telegramChatResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(5)
    public void testSuccessesAuthorizationChatStateInAuthCommand() throws JsonProcessingException {

        log.debug("Тесты успешной авторизации юзера");

        //Given
        String verificationCode = generationService.generateEmailVerificationCode();
        String encodeVerificationCode = passwordEncoder.encode(verificationCode);

        Update update = createTelegramUpdate(verificationCode);

        String expectedMsgText = MessageProvider.getSuccessesAuthorizationMsg(
                UpdateUtilsService.getTelegramUserFullName(update)
        );

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        ApiResponse<VerificationCodeDTO> verificationCodeApiResponse = responses.get(VERIFICATION_CODE.getKeyValue());
        VerificationCodeDTO verificationCodeDTO = verificationCodeApiResponse.getData();

        verificationCodeDTO.setOtpHash(encodeVerificationCode);

        verificationCodeApiResponse.setData(verificationCodeDTO);

        //When
        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/otp_code/telegram_user_id/" + TELEGRAM_USER_FOR_TESTS.getTelegramUserId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(verificationCodeApiResponse)));

        mockTelegramChatAddRequest(telegramChatResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(6)
    public void testUnSuccessesAuthorizationChatStateInAuthCommandWithExpiredOtpCode() throws JsonProcessingException {

        log.debug("Тесты проверка верификационного кода с истёкшим сроком");

        //Given
        String verificationCode = generationService.generateEmailVerificationCode();
        String encodeVerificationCode = passwordEncoder.encode(verificationCode);

        Update update = createTelegramUpdate(verificationCode);

        String expectedMsgText = WarnMessageProvider.EXPIRED_VERIFICATION_CODE;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        ApiResponse<VerificationCodeDTO> verificationCodeApiResponse = responses.get(VERIFICATION_CODE.getKeyValue());
        VerificationCodeDTO verificationCodeDTO = verificationCodeApiResponse.getData();

        verificationCodeDTO.setOtpHash(encodeVerificationCode);
        verificationCodeDTO.setExpiresAt(LocalDateTime.now().minusDays(5L));

        verificationCodeApiResponse.setData(verificationCodeDTO);

        //When
        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/otp_code/telegram_user_id/" + TELEGRAM_USER_FOR_TESTS.getTelegramUserId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(verificationCodeApiResponse)));

        mockTelegramChatAddRequest(telegramChatResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(7)
    public void testUnSuccessesAuthorizationChatStateInAuthCommandWithNotValidOtpCode() throws JsonProcessingException {

        log.debug("Тесты не валидного верификационного кода");

        //Given
        String verificationCode = generationService.generateEmailVerificationCode();
        String fakeCode = generationService.generateEmailVerificationCode();
        String encodeVerificationCode = passwordEncoder.encode(verificationCode);

        Update update = createTelegramUpdate(fakeCode);

        String expectedMsgText = WarnMessageProvider.NOT_VALID_VERIFICATION_CODE;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        ApiResponse<VerificationCodeDTO> verificationCodeApiResponse = responses.get(VERIFICATION_CODE.getKeyValue());
        VerificationCodeDTO verificationCodeDTO = verificationCodeApiResponse.getData();

        verificationCodeDTO.setOtpHash(encodeVerificationCode);
        verificationCodeDTO.setExpiresAt(LocalDateTime.now().minusDays(5L));

        verificationCodeApiResponse.setData(verificationCodeDTO);

        //When
        telegramUserAuthFilterMockRequests();
        telegramUserAuthFilterMockSessionRequest();

        mockTelegramChatGetByIdRequest(telegramChatForTests.getId(), telegramChatResponse);

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/otp_code/telegram_user_id/" + TELEGRAM_USER_FOR_TESTS.getTelegramUserId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(verificationCodeApiResponse)));

        mockTelegramChatAddRequest(telegramChatResponse);

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSendMessage();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }
    @Test
    @Order(9)
    public void testResponseWithEmptyBodyRequest() throws JsonProcessingException {
        log.debug("Тесты ответа с пустым запросом");

        ApiResponse expectedResponse = ApiResponseUtilsService.fail(REQUEST_BODY_DO_NOT_CONTAINS_TELEGRAM_UPDATE);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String expected = objectWriter.writeValueAsString(expectedResponse);

        client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .json(expected);
    }
    @Test
    @Order(10)
    public void testResponseWithEmptyBodyJsonRequest() throws JsonProcessingException {
        log.debug("Тесты ответа с пустым телом запроса");

        ApiResponse expectedResponse = ApiResponseUtilsService.fail(REQUEST_BODY_DO_NOT_CONTAINS_TELEGRAM_UPDATE);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String expected = objectWriter.writeValueAsString(expectedResponse);

        client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(" "))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .json(expected);
    }
    @Test
    @Order(1000)
    public void testWithInternalDatabaseServer() throws JsonProcessingException {

        //Given
        String expectedMsg =
                WarnMessageProvider.getSorryMsg("бот временно недоступен, попробуйте написать позже!");
        Update update = createTelegramUpdate("fff");

        String requestBody = objectMapper.writeValueAsString(update);

        mockServerContainer.stop();

        WebTestClient.ResponseSpec exchange = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchange.expectStatus().is2xxSuccessful();

        ArgumentCaptor<Long> chatIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> messageTextCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(telegramBotMessageSender)
                .sendMessage(chatIdCaptor.capture(), messageTextCaptor.capture());

        String actualMsg = messageTextCaptor.getValue();

        assertEquals(expectedMsg, actualMsg);

        mockServerContainer.start();
        mockServerClient = new MockServerClient(
                mockServerContainer.getHost(),
                mockServerContainer.getServerPort()
        );

    }


    private Update createTelegramUpdate(String msgText) {

        Update result = new Update();
        Message message = new Message();
        message.setText(msgText);
        message.setChat(TELEGRAM_API_CHAT_FOR_TESTS);
        message.setFrom(TELEGRAM_API_USER_FOR_TESTS);

        result.setUpdateId(TELEGRAM_UPDATE_ID);
        result.setMessage(message);

        return result;
    }
    private void telegramUserAuthFilterMockRequests() throws JsonProcessingException {

        ApiResponse telegramChatResponse = responses.get(TELEGRAM_CHAT.getKeyValue());

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user_id/" + TELEGRAM_API_USER_FOR_TESTS.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        ApiResponse<UserDTO> userDTOApiResponse = responses.get(USER.getKeyValue());

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/telegram_user_id/" + TELEGRAM_API_USER_FOR_TESTS.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(userDTOApiResponse)));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/username/" + getUserForTests().getUsername()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(userDTOApiResponse)));
    }
    private void telegramUserAuthFilterMockSessionRequest() throws JsonProcessingException {
        JwtTelegramDataImpl jwtDataForPersistentToken = JwtTelegramDataImpl.builder()
                .userDetails(USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS)
                .expirationTime(DateTimeService.convertDaysToMillis(persistentSessionExpirationTime))
                .subjects(
                        Map.of(
                                JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(),
                                TELEGRAM_USER_FOR_TESTS.getTelegramUserId()
                        )
                )
                .build();

        JwtTelegramDataImpl jwtDataForTransientToken = JwtTelegramDataImpl.builder()
                .userDetails(USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS)
                .expirationTime(DateTimeService.convertMinutesToMillis(transientSessionExpirationTime))
                .subjects(
                        Map.of(
                                JWTDataSubjectKeys.TELEGRAM_USER_ID.getSubjectKey(),
                                TELEGRAM_USER_FOR_TESTS.getTelegramUserId()
                        )
                )
                .build();

        String persistentToken = jwtService.generateToken(jwtDataForPersistentToken);
        String transientToken = jwtService.generateToken(jwtDataForTransientToken);

        PersistentSession persistentSession = PersistentSession.builder()
                .id(45678L)
                .data(persistentToken)
                .isActive(true)
                .telegramSession(TELEGRAM_SESSION_FOR_TESTS)
                .build();

        TransientSession transientSession = TransientSession.builder()
                .id(56789L)
                .data(transientToken)
                .isActive(true)
                .telegramSession(TELEGRAM_SESSION_FOR_TESTS)
                .build();

        TelegramSessionDTO telegramSessionDTO =
                mapperService.toDTO(TELEGRAM_SESSION_FOR_TESTS, TelegramSessionDTO.class);
        PersistentSessionDTO persistentSessionDTO = mapperService.toDTO(persistentSession, PersistentSessionDTO.class);
        TransientSessionDTO transientSessionDTO = mapperService.toDTO(transientSession, TransientSessionDTO.class);

        ApiResponse<TelegramSessionDTO> telegramSessionResponse = ApiResponse.<TelegramSessionDTO>builder()
                .data(telegramSessionDTO)
                .includeObject(TELEGRAM_USER.getKeyValue(), TELEGRAM_USER_FOR_TESTS)
                .includeList(PERSISTENT_SESSION.getKeyValue(), List.of(persistentSessionDTO))
                .includeList(TRANSIENT_SESSION.getKeyValue(), List.of(transientSessionDTO))
                .build();

        ApiResponse<PersistentSessionDTO> persistentSessionResponse = ApiResponse.<PersistentSessionDTO>builder()
                .data(persistentSessionDTO)
                .build();

        mockTelegramSessionByTelegramUserIdGetRequest(telegramSessionResponse.getData().getId(), telegramSessionResponse);

        mockPersistentSessionAddRequest(persistentSessionResponse);
    }
    private void mockTelegramChatGetByIdRequest(Long id, ApiResponse<?> telegramChatResponse) throws JsonProcessingException {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));
    }
    private void mockTelegramChatAddRequest(ApiResponse<?> telegramChatResponse) throws JsonProcessingException {
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/chat/add/")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse))
                );
    }
    private void mockPersistentSessionAddRequest(ApiResponse<?> persistentSessionResponse) throws JsonProcessingException {
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/persistent-session/add/")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(
                                objectMapper.writeValueAsString(persistentSessionResponse)
                        )
                );
    }
    private void mockTelegramSessionByTelegramUserIdGetRequest(Long telegramUserId, ApiResponse<?> telegramSessionResponse) throws JsonProcessingException {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/session/telegram_user_id/")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramSessionResponse))
                );
    }
    private void mockTelegramSessionSaveRequest(ApiResponse<?> response) throws JsonProcessingException {
        mockServerClient.when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/session/add/")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withBody(objectMapper.writeValueAsString(response))
                );
    }
    private void mockTelegramUserWithTelegramSessionGetRequest(Long telegramUserId, ApiResponse<?> telegramUserResponse) throws JsonProcessingException {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/telegram_user/telegram_session/" + telegramUserId)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withBody(objectMapper.writeValueAsString(telegramUserResponse))
                );
    }

    private SendMessage captorSendMessage() {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        Mockito.verify(telegramBotMessageSender).sendMessage(captor.capture());

        return captor.getValue();
    }

    private void printToConsoleRegisteredMockRequests() {
        log.debug("Зарегистрированые GET запросы mockServerClient");
        Expectation[] getExpectations = mockServerClient.retrieveActiveExpectations(request().withMethod("GET"));

        for (Expectation expectation : getExpectations) {
            log.debug("Запрос = {}", expectation);
        }

        log.debug("Зарегистрированые POST запросы mockServerClient");
        Expectation[] postExpectations = mockServerClient.retrieveActiveExpectations(request().withMethod("POST"));

        for (Expectation expectation : postExpectations) {
            log.debug("Запрос = {}", expectation);
        }
    }


    @Test
    public void testJwtService() {

        JwtDataProvideDataImpl jwtTelegramData = JwtDataProvideDataImpl.builder()
                .username("telegram-bot-service")
                .subjects(Map.of("username", "telegram-bot-service"))
                .expirationTime(DateTimeService.convertDaysToMillis(30L))
                .build();

        String token = jwtService.generateToken(jwtTelegramData);

        log.debug(token);

    }

    /*
    @Test
        public void testSendingEmailMessage() {
            try {
                emailService.sendSimpleMail(
                        "xisi926@ukr.net",
                        "Код активации",
                        "Привет, вот твой код активации - 5555"
                );
            } catch (Exception e) {
                log.debug("Сообщение не отправлено по причине {}", e.getMessage(), e);
            }
        }
*/

}
