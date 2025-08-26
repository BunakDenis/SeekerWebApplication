package com.example.telegram;

import com.example.data.models.consts.RequestMessageProvider;
import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.utils.file.loader.EnvLoader;
import com.example.utils.generator.GenerationService;
import com.example.utils.sender.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.telegram.constanst.TelegramBotConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
@EnableReactiveMethodSecurity
@Testcontainers
@Slf4j
public class TestSpring {

    @Autowired
    private WebTestClient client;

    private static Dotenv dotenv;

    private static ObjectMapper objectMapper;

    private static ModelMapperService mapperService;

    private Map<String, ApiResponse> responses;

    @MockBean
    private TelegramBotMessageSender telegramBotMessageSender;

    @Autowired
    private EmailService emailService;

    @Container
    static MockServerContainer mockServerContainer =
            new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));

    static MockServerClient mockServerClient;

    static {
        dotenv = EnvLoader.DOTENV;
        mapperService = new ModelMapperService(new ModelMapper());
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mockServerClient = new MockServerClient(
                mockServerContainer.getHost(),
                mockServerContainer.getServerPort()
        );

        log.debug("MockServerContainer endpoint = {}", mockServerContainer.getEndpoint());

        registry.add("data.provide.api.url", mockServerContainer::getEndpoint);
    }

    @BeforeAll
    public static void init() {
        log.debug("init");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    @BeforeEach
    public void initVariables() {
        responses = new LinkedHashMap<>();

        ApiResponse<UserDTO> userDTOApiResponse = new ApiResponse<>(
                HttpStatus.OK,
                RequestMessageProvider.SUCCESSES_MSG,
                mapperService.toDTO(USER_FOR_TESTS, UserDTO.class)
        );

        ApiResponse<TelegramChatDTO> telegramChatResponse = new ApiResponse<>(
                HttpStatus.OK,
                RequestMessageProvider.SUCCESSES_MSG,
                mapperService.toDTO(telegramChatForTests, TelegramChatDTO.class)
        );

        telegramChatResponse.addIncludeObject("telegram_user", TELEGRAM_USER_FOR_TESTS);

        responses.put("userDTOApiResponse", userDTOApiResponse);
        responses.put("telegramChatResponse", telegramChatResponse);
    }

    @AfterEach
    public void afterEach() {
        mockServerClient.reset();
    }

    @Test
    public void testUnknownCommand() throws JsonProcessingException {
        //Given
        Update update = new Update();
        Message message = createTelegramMessage("fff");
        update.setMessage(message);

        String expectedMsgText = MessageProvider.UNKNOWN_COMMAND_OR_QUERY;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        //When
        telegramUserAuthFilterMockRequests();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/get/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        WebTestClient.ResponseSpec exchange = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchange.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSenMessage();

        //Then
        assertEquals(expectedMsgText, actual.getText());
    }

    @Test
    public void testStartCommand() throws Exception {

        log.debug("testStartCommand");

        //Given
        Update update = new Update();
        Message message = createTelegramMessage(Commands.START.getCommand());
        update.setMessage(message);

        String expectedMsgText = message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + "! " +
                MessageProvider.START_MSG;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        //When
        telegramUserAuthFilterMockRequests();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/get/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        WebTestClient.ResponseSpec exchange = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchange.expectStatus().is2xxSuccessful();

        SendMessage actual = captorSenMessage();

        assertTrue(actual.getReplyMarkup() instanceof ReplyKeyboardMarkup);

        ReplyKeyboardMarkup markup = (ReplyKeyboardMarkup) actual.getReplyMarkup();

        List<KeyboardRow> keyboard = markup.getKeyboard();

        assertEquals("Декодировать аудио", keyboard.get(0).get(0).getText());

        //Then
        assertEquals(Long.toString(message.getChatId()), actual.getChatId());
        assertEquals(expectedMsgText, actual.getText());

    }

    @Test
    public void testAuthCommand() throws JsonProcessingException {

        log.debug("testAuthCommand()");

        //Given
        Update update = new Update();
        Message message = createTelegramMessage(Commands.AUTHORIZE.getCommand());
        update.setMessage(message);

        String expectedMsgText = MessageProvider.EMAIL_CHECKING_MSG;

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());

        telegramChatResponse.setData(telegramChatDTO);

        //When
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/get/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/chat/add/"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

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
    public void testNotValidInputEmailChatStateInAuthCommand() throws JsonProcessingException {
        //Given
        String testMsg = "test";
        Update update = new Update();
        Message message = createTelegramMessage(testMsg);
        update.setMessage(message);

        String expectedMsgText = WarnMessageProvider.getNotValidEmailAddress(testMsg);

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.ENTER_EMAIL.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        //When
        telegramUserAuthFilterMockRequests();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/get/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/chat/add/"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        Mockito.verify(telegramBotMessageSender, Mockito.timeout(5000L)).sendMessage(captor.capture());

        SendMessage actual = captor.getValue();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }

    @Test
    public void testVerificationGenerationCode() {
        GenerationService generationService = new GenerationService();

        String code = generationService.generateEmailVerificationCode();

        log.debug(code);
    }

    @Test
    public void testSuccessesAuthorizationChatStateInAuthCommand() throws JsonProcessingException {

        log.debug("testAuthCommand()");

        //Given
        Update update = new Update();
        Message message = createTelegramMessage("2545678");
        update.setMessage(message);

        String expectedMsgText = MessageProvider.getSuccessesAuthorizationMsg(
                TELEGRAM_API_USER_FOR_TESTS.getFirstName(),
                TELEGRAM_API_USER_FOR_TESTS.getLastName()
        );

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        //When
        telegramUserAuthFilterMockRequests();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/get/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/chat/add/"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

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

    /*
    @Test
    public void testInputEmailChatStateInAuthCommand() throws JsonProcessingException {
        //Given
        String testMsg = "test@mail.com";
        Update update = new Update();
        Message message = createTelegramMessage(testMsg);
        update.setMessage(message);

        String expectedMsgText = MessageProvider.getEmailVerificationMsg(testMsg);

        String requestBody = objectMapper.writeValueAsString(update);

        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        TelegramChatDTO telegramChatDTO = telegramChatResponse.getData();
        telegramChatDTO.setUiElement(UiElements.COMMAND.getUiElement());
        telegramChatDTO.setUiElementValue(Commands.AUTHORIZE.getCommand());
        telegramChatDTO.setChatState(DialogStates.ENTER_EMAIL.getDialogState());

        telegramChatResponse.setData(telegramChatDTO);

        //When
        telegramUserAuthFilterMockRequests();

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/chat/telegram_user/get/" + telegramChatForTests.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        TelegramChatDTO responseTelegramChat = telegramChatResponse.getData();
        responseTelegramChat.setUiElement(UiElements.COMMAND.getUiElement());
        responseTelegramChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        responseTelegramChat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());

        telegramChatResponse.setData(responseTelegramChat);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/api/v1/chat/add/"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(telegramChatResponse)));

        WebTestClient.ResponseSpec exchangeAuthCommand = client.post()
                .uri("/api/bot/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange();

        exchangeAuthCommand.expectStatus().is2xxSuccessful();

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        Mockito.verify(telegramBotMessageSender, Mockito.timeout(5000L)).sendMessage(captor.capture());

        SendMessage actual = captor.getValue();

        //Then
        assertEquals(expectedMsgText, actual.getText());

    }
*/

    private Message createTelegramMessage(String msgText) {

        Message message = new Message();
        message.setText(msgText);
        message.setChat(TELEGRAM_API_CHAT_FOR_TESTS);
        message.setFrom(TELEGRAM_API_USER_FOR_TESTS);

        return message;
    }

    private void telegramUserAuthFilterMockRequests() throws JsonProcessingException {

        ApiResponse<UserDTO> userDTOApiResponse = responses.get("userDTOApiResponse");
        ApiResponse<TelegramChatDTO> telegramChatResponse = responses.get("telegramChatResponse");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/get/telegram_user_id/" + TELEGRAM_API_USER_FOR_TESTS.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(userDTOApiResponse)));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/user/get/username/" + USER_FOR_TESTS.getUsername()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(userDTOApiResponse)));
    }

    private SendMessage captorSenMessage() {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        Mockito.verify(telegramBotMessageSender,Mockito.timeout(5000L)).sendMessage(captor.capture());

        return captor.getValue();
    }

}
