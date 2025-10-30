package com.example.database.service;

import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.entity.Curator;
import com.example.data.models.entity.dto.CuratorDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.database.repo.jpa.CuratorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;
import static com.example.data.models.utils.EntityUtilsService.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuratorService {

    private final CuratorRepo repo;
    private final ModelMapperService mapperService;

    public ApiResponse<CuratorDTO> save(Curator curator) {
        Curator save = repo.save(curator);

        return success(mapperService.toDTO(save, CuratorDTO.class));
    }

    public ApiResponse<CuratorDTO> update(Curator curator) {
        return save(curator);
    }

    public ApiResponse<CuratorDTO> getById(Long id) {
        Optional<Curator> found = repo.findById(id);

        if (!found.isPresent()) throw new EntityNotFoundException("Curator with id=" + id + "is not found", new Curator());

        return success(mapperService.toDTO(found.get(), CuratorDTO.class));
    }

    public ApiResponse<Boolean> existsById(Long id) {
        boolean result = repo.existsById(id);

        return success(result);
    }

    public ApiResponse<Boolean> delete(Long id) {
        repo.deleteById(id);

        return success(Boolean.TRUE);
    }

}
