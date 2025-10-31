package com.example.database.service;

import com.example.data.models.entity.dto.DiscipleDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.service.ModelMapperService;
import com.example.database.entity.Disciple;
import com.example.database.repo.jpa.DiscipleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscipleService {

    private final DiscipleRepo repo;
    private final ModelMapperService mapperService;

    public ApiResponse<DiscipleDTO> save(Disciple disciple) {
        Disciple savedDisciple = repo.save(disciple);

        return success(mapperService.toDTO(savedDisciple, DiscipleDTO.class));
    }

    public ApiResponse<DiscipleDTO> update(Disciple disciple) {
        return save(disciple);
    }

    public ApiResponse<DiscipleDTO> getById(Long id) {
        Optional<Disciple> found = repo.findById(id);

        if (!found.isPresent()) throw new EntityNotFoundException(
                "Disciple with id=" + id + " is not found",
                new Disciple()
        );

        return success(mapperService.toDTO(found.get(), DiscipleDTO.class));

    }

    public ApiResponse<Boolean> existsById(Long id) {
        boolean exists = repo.existsById(id);

        return success(exists);
    }

    public ApiResponse<Boolean> existsByCuratorId(Long id) {
        Boolean exists = repo.existsByCuratorId(id);

        return success(exists);
    }

    public ApiResponse<Boolean> delete(Long id) {
        repo.deleteById(id);

        return success(true);
    }

}
