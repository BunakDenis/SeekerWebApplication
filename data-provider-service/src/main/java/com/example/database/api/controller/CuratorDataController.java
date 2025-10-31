package com.example.database.api.controller;

import com.example.data.models.entity.dto.CuratorDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.entity.Curator;
import com.example.database.service.CuratorService;
import com.example.data.models.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class CuratorDataController {

    private final CuratorService curatorService;
    private final ModelMapperService mapperService;

    @PostMapping(path = {"/curator/save/", "/curator/save"})
    public ResponseEntity<ApiResponse<CuratorDTO>> save(
            @RequestBody ApiRequest<CuratorDTO> request
            ) {

        Curator curator = mapperService.toEntity(request.getData(), Curator.class);

        return ResponseEntity.ok().body(curatorService.save(curator));
    }

    @PostMapping(path = {"/curator/update/", "/curator/update"})
    public ResponseEntity<ApiResponse<CuratorDTO>> update(
            @RequestBody ApiRequest<CuratorDTO> request
    ) {
        Curator curator = mapperService.toEntity(request.getData(), Curator.class);

        return ResponseEntity.ok().body(curatorService.update(curator));
    }

    @GetMapping("/curator/{id}")
    public ResponseEntity<ApiResponse<CuratorDTO>> getById(
            @PathVariable(name = "id") Long id
    ) {
        try {
            return ResponseEntity.ok().body(curatorService.getById(id));
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(400).body(ApiResponse.<CuratorDTO>builder().message(e.getMessage()).build());
        }

    }

    @GetMapping("/curator/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> existsById(
            @PathVariable(name = "id") Long id
    ) {
        ApiResponse<Boolean> response;

        try {
            response = curatorService.existsById(id);
            return ResponseEntity.ok().body(response);
        } catch (EntityNotFoundException e) {
            response = ApiResponse.<Boolean>builder().data(false).build();
            return ResponseEntity.ok().body(response);
        }

    }

    @PostMapping("/curator/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok().body(curatorService.delete(id));
    }

}
