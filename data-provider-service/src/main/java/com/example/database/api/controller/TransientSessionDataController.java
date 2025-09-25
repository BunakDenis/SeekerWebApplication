package com.example.database.api.controller;


import com.example.database.entity.TransientSession;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TransientSessionDTO;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.TransientSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TransientSessionDataController {

    private final TransientSessionService sessionService;
    private final ModelMapperService mapperService;

    @PostMapping({"/transient-session/add/", "/transient-session/add"})
    public ResponseEntity<ApiResponse<TransientSessionDTO>> save(
            @RequestBody ApiRequest<TransientSessionDTO> request
    ) {

        TransientSession transientSession = mapperService.toEntity(request.getData(), TransientSession.class);

        ApiResponse<TransientSessionDTO> response = sessionService.save(transientSession);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping({"/transient-session/update/", "/transient-session/update"})
    public ResponseEntity<ApiResponse<TransientSessionDTO>> update (
            @RequestBody ApiRequest<TransientSessionDTO> request
    ) {

        ApiResponse<TransientSessionDTO> response =
                sessionService.update(mapperService.toEntity(request.getData(), TransientSession.class));

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/transient-session/{id}")
    public ResponseEntity<ApiResponse<TransientSessionDTO>> getById(
            @PathVariable("id") Long id
    ) {
        ApiResponse<TransientSessionDTO> response = sessionService.getById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/transient-session/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable("id") Long id
    ) {
        ApiResponse<Boolean> response = sessionService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
