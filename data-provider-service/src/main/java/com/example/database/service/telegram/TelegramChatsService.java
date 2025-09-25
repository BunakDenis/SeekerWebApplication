package com.example.database.service.telegram;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramChatRepo;
import com.example.database.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramChatsService {


    private final TelegramChatRepo chatRepo;
    private final ModelMapperService mapperService;


    public ApiResponse<TelegramChatDTO> create(TelegramChat chat) {

        TelegramChat savedChat = chatRepo.save(chat);

        TelegramChatDTO dto = mapperService.toDTO(savedChat, TelegramChatDTO.class);

        return success(HttpStatus.CREATED, dto);
    }
    public ApiResponse<TelegramChatDTO> update(TelegramUserDTO dto) {
        TelegramChat telegramChat = mapperService.toEntity(dto, TelegramChat.class);

        ApiResponse<TelegramChatDTO> response = create(telegramChat);

        response.setStatus(HttpStatus.OK);

        return response;

    }

    /*
        TODO
            1. Добавить метод получение чата по id без TelegramUser
     */

    public ApiResponse getTelegramChatByIdWithTelegramUser(Long id) {

        Optional<TelegramChat> findChat = chatRepo.findFirstByTelegramChatIdOrderByIdDesc(id);

        if (findChat.isPresent()) {

            TelegramChat chat = findChat.get();
            TelegramChatDTO telegramChatDTO = mapperService.toDTO(chat, TelegramChatDTO.class);
            TelegramUser telegramUser = chat.getTelegramUser();
            TelegramUserDTO telegramUserDTO = mapperService.toDTO(telegramUser, TelegramUserDTO.class);

            ApiResponse<TelegramChatDTO> response = success(telegramChatDTO);

            response.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

            return response;
        }

        return fail(ResponseMessageProvider.getEntityNotFoundMessage(new TelegramChat()));
    }
    public ApiResponse getTelegramChatByTelegramUserIdWithTelegramUser(Long id) {

        log.debug("Метод getTelegramChatByIdWithTelegramUser {}", id);

        Optional<TelegramChat> findChat = chatRepo.findFirstByTelegramUser_TelegramUserIdOrderByIdDesc(id);

        log.debug("findChat.isPresent() = {}", findChat.isPresent());

        if (findChat.isPresent()) {

            TelegramChat chat = findChat.get();

            log.debug(chat.getTelegramUser().toString());

            TelegramUser telegramUser = chat.getTelegramUser();
            TelegramUserDTO telegramUserDTO = mapperService.toDTO(telegramUser, TelegramUserDTO.class);

            log.debug("Метод getTelegramChatByTelegramUserId, telegramUser = {}", telegramUser);

            TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);

            ApiResponse<TelegramChatDTO> response = success(dto);

            response.addIncludeObject("telegram_user", telegramUserDTO);

            return response;
        }

        return ApiResponseUtilsService.fail("Telegram chat of telegram user with id = " + id + ", not found");

    }
    public ApiResponse<TelegramChatDTO> getTelegramChatByTelegramUserId(Long id) {

        Optional<TelegramChat> findChat = chatRepo.findFirstByTelegramUser_TelegramUserIdOrderByIdDesc(id);

        if (!findChat.isPresent())
            throw new EntityNotFoundException(
                    "Telegram chat by telegram_user_id=" + id + "is not found",
                    new TelegramChat()
            );

        TelegramChat chat = findChat.get();

        return success(mapperService.toDTO(chat, TelegramChatDTO.class));

    }
    public ApiResponse<TelegramChatDTO> getAllTelegramChatById(Long id) {

        List<TelegramChatDTO> dtoList = new ArrayList<>();

        List<Object> chats = Collections.singletonList(chatRepo.getAllById(id));

        ApiResponse<TelegramChatDTO> response = success(null);

        response.addIncludeListObjects("telegram_chat", chats);

        return response;
    }
    public ApiResponse<Boolean> delete(Long id) {
        chatRepo.deleteById(id);

        return success(true);

    }

}
