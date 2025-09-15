package com.example.telegram.exception;


import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

//@Component
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {


    private TelegramBotMessageSender sender;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties.Resources resources,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer codecConfigurer,
                                  TelegramBotMessageSender sender) {
        super(errorAttributes, resources, applicationContext);
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setMessageReaders(codecConfigurer.getReaders());
        this.sender = sender;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        log.error("Global error handler поймал исключение для {}", request.path());

        return request.bodyToMono(Update.class)
                .flatMap(upd -> {

                    log.debug(upd.toString());

                    return Mono.just(upd);
                })
                .then(ServerResponse.ok().bodyValue("Извините, произошла ошибка. Повторите запрос позже."));
    }
}
