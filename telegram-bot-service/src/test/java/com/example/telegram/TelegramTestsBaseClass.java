package com.example.telegram;


import com.example.data.models.service.JWTService;
import com.example.data.models.utils.generator.GenerationService;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.utils.sender.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive"
)
@AutoConfigureWebTestClient
@EnableReactiveMethodSecurity
@Testcontainers
public abstract class TelegramTestsBaseClass {

    @Autowired
    protected WebTestClient client;
    @Autowired
    protected JWTService jwtService;
    @Autowired
    protected GenerationService generationService;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ModelMapperService mapperService;
    @MockBean
    protected TelegramBotMessageSender telegramBotMessageSender;
    @MockBean
    protected EmailService emailService;

}
