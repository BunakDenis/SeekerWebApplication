package com.example.database.service.telegram;

import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.response.ApiResponseWithDataList;
import com.example.database.entity.TelegramChat;
import com.example.database.repo.telegram.TelegramChatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramChatsService {

    private final TelegramChatRepo chatRepo;

    private final ModelMapperService mapperService;

    public ApiResponse<TelegramChatDTO> create(TelegramChat chat) {

        TelegramChat savedChat = chatRepo.save(chat);

        TelegramChatDTO result = mapperService.toDTO(savedChat, TelegramChatDTO.class);

        return new ApiResponse(HttpStatus.OK, HttpStatus.OK.toString(), result);
    }

    public ApiResponse<TelegramChatDTO> getTelegramChatById(Long id) {
        List<TelegramChat> all = chatRepo.getAllById(id);

        TelegramChat chat = all.get(all.size() - 1);

        TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);

        return new ApiResponse(
                HttpStatus.OK,
                "Последний чат получен успешно!",
                dto);
    }

    public ApiResponseWithDataList<TelegramChatDTO> getAllTelegramChatById(Long id) {

        List<TelegramChatDTO> dtoList = new ArrayList<>();

        List<TelegramChat> chats = chatRepo.getAllById(id);

        chats.forEach(chat -> {
            dtoList.add(mapperService.toDTO(chat, TelegramChatDTO.class));
        });

        return new ApiResponseWithDataList(HttpStatus.OK, HttpStatus.OK.toString(), dtoList);
    }

}
