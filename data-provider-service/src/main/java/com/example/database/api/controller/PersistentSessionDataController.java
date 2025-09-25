package com.example.database.api.controller;

import com.example.database.entity.PersistentSession;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.telegram.PersistentSessionDTO;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.PersistentSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class PersistentSessionDataController {

    private final PersistentSessionService sessionService;
    private final ModelMapperService mapperService;

    @PostMapping({"/persistent-session/add/", "/persistent-session/add"})
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> save(
            @RequestBody ApiRequest<PersistentSessionDTO> request
    ) {

        PersistentSession persistentSession = mapperService.toEntity(request.getData(), PersistentSession.class);

        ApiResponse<PersistentSessionDTO> response = sessionService.save(persistentSession);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping({"/persistent-session/update/", "/persistent-session/update"})
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> update (
            @RequestBody ApiRequest<PersistentSessionDTO> request
    ) {

        ApiResponse<PersistentSessionDTO> response =
                sessionService.update(mapperService.toEntity(request.getData(), PersistentSession.class));

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/persistent-session/{id}")
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> getById(
            @PathVariable("id") Long id
    ) {
        ApiResponse<PersistentSessionDTO> response = sessionService.getById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/persistent-session/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable("id") Long id
    ) {
        ApiResponse<Boolean> response = sessionService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
