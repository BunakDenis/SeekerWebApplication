package com.example.database.service.telegram;


import com.example.data.models.consts.ExceptionMessageProvider;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.PersistentSessionDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.NotActiveSessionException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.telegram.PersistentSession;
import com.example.database.repo.jpa.telegram.PersistentSessionRepo;
import com.example.data.models.service.ModelMapperService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
@Data
public class PersistentSessionService {

    private final PersistentSessionRepo repo;

    private final ModelMapperService mapperService;

    public ApiResponse<PersistentSessionDTO> save(PersistentSession session) {

        List<PersistentSession> persistentSessions = repo.findAllByTelegramSessionId(session.getTelegramSession().getId());

        //Деактивируем предідущие сессии
        if (!persistentSessions.isEmpty()) persistentSessions.forEach(prSession -> {
            prSession.setActive(false);
            update(prSession);
        });

        PersistentSession savedSession = repo.save(session);

        return ApiResponseUtilsService.success(mapperService.toDTO(savedSession, PersistentSessionDTO.class));

    }
    public ApiResponse<PersistentSessionDTO> update(PersistentSession session) {

        PersistentSession savedSession = repo.save(session);

        return ApiResponseUtilsService.success(mapperService.toDTO(savedSession, PersistentSessionDTO.class));
    }
    public ApiResponse<PersistentSessionDTO> getById(Long id) {
        Optional<PersistentSession> optionalSession = repo.findById(id);

        if (optionalSession.isPresent()) {
            PersistentSession result = optionalSession.get();

            return ApiResponseUtilsService.success(mapperService.toDTO(result, PersistentSessionDTO.class));
        }

        throw new EntityNotFoundException("Persistent session with id=" + id + " is not found", PersistentSession.class);

    }
    public ApiResponse<PersistentSessionDTO> getActiveByTelegramUserId(Long id) {

        Optional<PersistentSession> optionalPersistentSession = repo.findActiveByTelegramUserId(id);

        if (!optionalPersistentSession.isPresent()) throw new NotActiveSessionException(
                ExceptionMessageProvider.getNotActiveSessionMsg("Persistent session", id)
        );

        PersistentSession persistentSession = optionalPersistentSession.get();

        return success(mapperService.toDTO(persistentSession, PersistentSessionDTO.class));

    }
    public ApiResponse<Boolean> delete(Long id) {
        repo.deleteById(id);

        return ApiResponseUtilsService.success(true);
    }

}
