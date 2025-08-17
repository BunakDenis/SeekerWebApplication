package com.example.database.service.telegram;

import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.response.ApiResponseWithDataList;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramChatRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.database.consts.RequestMessageProvider.SUCCESSES_MSG;

@Service
@RequiredArgsConstructor
public class TelegramChatsService {

    private final TelegramChatRepo chatRepo;

    private final ModelMapper modelMapper;

    public ApiResponse<TelegramChatDTO> create(TelegramChat chat) {
        TelegramChat savedChat = chatRepo.save(chat);

        TelegramUser telegramUser = savedChat.getTelegramUser();

        TelegramUserDTO telegramUserDTO = TelegramUserDTO.builder()
                .id(telegramUser.getId())
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .username(telegramUser.getUsername())
                .isActive(telegramUser.isActive())
                .build();

        TelegramChatDTO result = TelegramChatDTO.builder()
                .id(savedChat.getId())
                .uiElement(savedChat.getUiElement())
                .chatState(savedChat.getChatState())
                .telegramUserDTO(telegramUserDTO)
                .build();

        return new ApiResponse(HttpStatus.OK, HttpStatus.OK.toString(), result);
    }

    public ApiResponse<TelegramChatDTO> getTelegramChatById(Long id) {
        List<TelegramChat> all = chatRepo.getAllById(id);

        TelegramChat chat = all.get(all.size() - 1);

        TelegramChatDTO dto = toDto(chat);

        return new ApiResponse(
                HttpStatus.OK,
                "Последний чат получен успешно!",
                dto);
    }

    public ApiResponseWithDataList<TelegramChatDTO> getAllTelegramChatById(Long id) {

        List<TelegramChatDTO> dtoList = new ArrayList<>();

        List<TelegramChat> chats = chatRepo.getAllById(id);

        chats.forEach(chat -> {
            dtoList.add(toDto(chat));
        });

        return new ApiResponseWithDataList(HttpStatus.OK, HttpStatus.OK.toString(), dtoList);
    }

    public TelegramChat toEntity(TelegramChatDTO dto) {
        return modelMapper.map(dto, TelegramChat.class);
    }

    public TelegramChatDTO toDto(TelegramChat chat) {
        return modelMapper.map(chat, TelegramChatDTO.class);
    }

}
