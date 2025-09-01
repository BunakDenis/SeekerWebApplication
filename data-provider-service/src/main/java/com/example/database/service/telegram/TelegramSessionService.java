package com.example.database.service.telegram;


import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.database.entity.TelegramSession;
import com.example.database.repo.telegram.TelegramSessionRepo;
import com.example.database.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
public class TelegramSessionService {

    private final TelegramSessionRepo repo;
    private final ModelMapperService mapperService;

    public ApiResponse<TelegramSessionDTO> create(TelegramSession session) {

        Optional<TelegramSession> save = Optional.of(repo.save(session));

        if (save.isPresent()) return success(
                    HttpStatus.CREATED,
                    mapperService.toDTO(save, TelegramSessionDTO.class)
            );

        throw new EntityNotSavedException(TelegramSession.class + " not saved");
    }
    public ApiResponse<TelegramSessionDTO> update(TelegramSession session) {

        ApiResponse<TelegramSessionDTO> response = create(session);

        response.setStatus(HttpStatus.OK);

        return response;
    }
    public ApiResponse<TelegramSessionDTO> findByTelegramUserId(Long telegramUserId) {

        Optional<TelegramSession> session = Optional.of(repo.getTelegramSessionByTelegramUserId(telegramUserId));

        if (session.isPresent())
            return success(mapperService.toDTO(session, TelegramSessionDTO.class));

        throw new EntityNotFoundException(TelegramSession.class + " not found", new TelegramSession());
    }
    public ApiResponse<TelegramSessionDTO> getSessionById(Long id) {
        Optional<TelegramSession> session = repo.findById(id);

        if (session.isPresent())
            return success(mapperService.toDTO(session.get(), TelegramSessionDTO.class));

        throw new EntityNotFoundException(TelegramSession.class + " not found", new TelegramSession());
    }
    public ApiResponse<Boolean> delete(Long id) {

        repo.deleteById(id);

        return success(Boolean.TRUE);
    }

}
