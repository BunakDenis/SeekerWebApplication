package com.example.database.service.telegram;


import com.example.data.models.consts.ExceptionMessageProvider;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TransientSessionDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.NotActiveSessionException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.TransientSession;
import com.example.database.repo.jpa.telegram.TransientSessionRepo;
import com.example.database.service.ModelMapperService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
@Data
public class TransientSessionService {

    private final TransientSessionRepo repo;

    private final ModelMapperService mapperService;

    public ApiResponse<TransientSessionDTO> save(TransientSession session) {

        List<TransientSession> transientSessions = repo.findAllByTelegramSessionId(session.getTelegramSession().getId());

        if (!transientSessions.isEmpty()) transientSessions.forEach(trSession -> {
            trSession.setActive(false);
            update(trSession);
        });

        TransientSession saveSession = repo.save(session);

        return success(mapperService.toDTO(saveSession, TransientSessionDTO.class));

    }
    public ApiResponse<TransientSessionDTO> update(TransientSession session) {

        TransientSession saveSession = repo.save(session);

        return success(mapperService.toDTO(saveSession, TransientSessionDTO.class));
    }
    public ApiResponse<TransientSessionDTO> getById(Long id) {

        Optional<TransientSession> optionalSession = repo.findById(id);

        if (optionalSession.isPresent()) {

            TransientSession session = optionalSession.get();

            return success(mapperService.toDTO(session, TransientSessionDTO.class));
        }

        throw new EntityNotFoundException("Transient session with id=" + id + " is not found", TransientSession.class);

    }
    public ApiResponse<TransientSessionDTO> getActiveById(Long id) {

        Optional<TransientSession> optionalTransientSession = repo.findActiveById(id);

        if (!optionalTransientSession.isPresent())
            throw new NotActiveSessionException(
                    ExceptionMessageProvider.getNotActiveSessionMsg("Transient session", id)
            );

        TransientSession transientSession = optionalTransientSession.get();

        return success(mapperService.toDTO(transientSession, TransientSessionDTO.class));
    }
    public ApiResponse<TransientSessionDTO> getActiveByTelegramUserId(Long id) {

        Optional<TransientSession> optionalTransientSession = repo.findActiveByTelegramUserId(id);

        if (!optionalTransientSession.isPresent())
            throw new NotActiveSessionException(
                    ExceptionMessageProvider.getNotActiveSessionMsg("Transient session", id)
            );

        TransientSession transientSession = optionalTransientSession.get();

        return success(mapperService.toDTO(transientSession, TransientSessionDTO.class));
    }
    public ApiResponse<Boolean> delete(Long id) {

        repo.deleteById(id);

        return ApiResponseUtilsService.success(true);

    }

}
