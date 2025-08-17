package com.example.database.service.telegram;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.database.entity.TelegramSession;
import com.example.database.repo.telegram.TelegramSessionRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.database.consts.RequestMessageProvider.NOT_FOUND_MSG;
import static com.example.database.consts.RequestMessageProvider.SUCCESSES_MSG;

@Service
@RequiredArgsConstructor
public class TelegramSessionService {

    private final TelegramSessionRepo repo;

    private final ModelMapper modelMapper;

    public ApiResponse<TelegramSessionDTO> getSessionById(Long id) {
        Optional<TelegramSession> session = repo.findById(id);

        if (session.isPresent()) {

            TelegramSessionDTO dto = toDTO(session.get());

            return new ApiResponse<>(HttpStatus.OK, SUCCESSES_MSG, dto);
        }
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, NOT_FOUND_MSG);
    }

    public ApiResponse<TelegramSessionDTO> create(TelegramSession session) {
        TelegramSession save = repo.save(session);

        TelegramSessionDTO telegramSessionDTO = toDTO(save);

        return new ApiResponse<>(HttpStatus.OK, SUCCESSES_MSG, telegramSessionDTO);
    }

    public ApiResponse<TelegramSessionDTO> update(TelegramSession session) {
        return create(session);
    }

    public ApiResponse<TelegramSessionDTO> findByTelegramUserId(Long telegramUserId) {

        TelegramSession session = repo.getTelegramSessionByTelegramUserId(telegramUserId);

        TelegramSessionDTO dto = toDTO(session);

        return new ApiResponse<>(HttpStatus.OK, SUCCESSES_MSG, dto);

    }

    public ApiResponse delete(TelegramSession session) {
        repo.delete(session);

        return new ApiResponse<>(HttpStatus.OK, SUCCESSES_MSG);
    }

    public TelegramSession toEntity(TelegramSessionDTO dto) {
        return modelMapper.map(dto, TelegramSession.class);
    }

    public TelegramSessionDTO toDTO(TelegramSession session) {
        return modelMapper.map(session, TelegramSessionDTO.class);
    }

}
