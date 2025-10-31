package com.example.server.handler;

import com.example.data.models.entity.VerificationCode;
import com.example.data.models.entity.jwt.JwtDataProvideDataImpl;
import com.example.data.models.service.JWTService;
import com.example.data.models.utils.generator.GenerationService;
import com.example.server.provider.ThymeleafModelObjectsProvider;
import com.example.server.service.UserService;
import com.example.utils.datetime.DateTimeService;
import com.example.utils.sender.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LinkedMap;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Этот класс содержит логику обработки запросов для наших страниц.
 * Он не является контроллером, а просто компонентом,
 * методы которого будут вызываться маршрутизатором.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PageHandler {


    private final UserService userService;
    private final EmailService emailService;
    private final GenerationService generationService;
    private final JWTService jwtService;


    /**
     * Обрабатывает GET-запрос на главную страницу.
     * * @param request объект запроса (здесь он используется правильно)
     * @return Mono<ServerResponse> с указанием, какую страницу отрендерить.
     */
    public Mono<ServerResponse> getMainPage(ServerRequest request) {
        return ServerResponse.ok().render("pages/index");
    }

    public Mono<ServerResponse> registerPage(ServerRequest request) {
        return ServerResponse.ok().render("pages/telegramUserSignUp");
    }

    public Mono<ServerResponse> successRegPage(ServerRequest request) {
        String userId = request.pathVariable("user_id");

        log.debug("Отдаю страницу успешной регистрации для пользователя с id={}", userId);

        Map<String, Object> mapForPage = new LinkedMap(ThymeleafModelObjectsProvider.getDefaultMapModelObjects());

        return userService.getByIdWithUserDetails(Long.valueOf(userId))
                .flatMap(user -> {

                    JwtDataProvideDataImpl jwtData = JwtDataProvideDataImpl.builder()
                            .username(user.getUsername())
                            .expirationTime(DateTimeService.convertDaysToMillis(1L))
                            .build();

                    String hashCode = generationService.generateEmailVerificationCode();
                    String token = jwtService.generateToken(jwtData);

                    VerificationCode code = VerificationCode.builder()
                            .expiresAt(LocalDateTime.now().plusDays(1L))
                            .otpHash(hashCode)
                            .dataAttribute(token)
                            .active(true)
                            .user(user)
                            .build();

                    log.debug("Verification code {}", code);

                    String subject = "Активация аккаунта в SeekerOffice.club";
                    String text = "Для активации аккаунта перейдите по ссылке\n\n" +
                            "https://truthseekeroffice.club/web-site/activate/" + hashCode;

                    emailService.sendSimpleMail(user.getEmail(),subject, text);

                    mapForPage.put("username", userService.getUserFullName(user));
                    mapForPage.put("email", user.getEmail());

                    return ServerResponse.ok().render("pages/successReg", mapForPage);
                });
    }

    public Mono<ServerResponse> activatedUserPage(ServerRequest request) {
        String activateCode = request.pathVariable("activate_code");

        log.debug("Activate code = {}", activateCode);

        return ServerResponse.ok().render("pages/index");
    }
}