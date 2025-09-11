package com.example.database.service.telegram;

import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramChatRepo;
import com.example.database.service.ModelMapperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public ApiResponse<TelegramChatDTO> getTelegramChatById(Long id) {

        List<TelegramChat> all = chatRepo.getAllById(id);

        TelegramChat chat = all.get(all.size() - 1);

        TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);

        return success(dto);
    }
    @Transactional
    public ApiResponse<TelegramChatDTO> getTelegramChatByIdWithTelegramUser(Long id) {

        List<TelegramChat> all = chatRepo.findByTelegramUserIdWithTelegramUser(id);

        TelegramChat chat = all.get(all.size() - 1);
        log.debug(chat.getTelegramUser().toString());
        TelegramUser telegramUser = chat.getTelegramUser();
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(telegramUser, TelegramUserDTO.class);

        log.debug("Метод getTelegramChatById, telegramUser = {}", telegramUser);

        TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);

        ApiResponse<TelegramChatDTO> response = success(dto);

        response.addIncludeObject("telegram_user", telegramUserDTO);

        return response;
    }
    public ApiResponse<TelegramChatDTO> getAllTelegramChatById(Long id) {

        List<TelegramChatDTO> dtoList = new ArrayList<>();

        List<Object> chats = Collections.singletonList(chatRepo.getAllById(id));

        ApiResponse<TelegramChatDTO> response = success(null);

        response.addIncludeListObjects("telegram_chat", chats);

        return response;
    }

}
