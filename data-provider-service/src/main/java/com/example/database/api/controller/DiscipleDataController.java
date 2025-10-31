package com.example.database.api.controller;

import com.example.data.models.entity.dto.DiscipleDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.database.entity.Disciple;
import com.example.database.service.DiscipleService;
import com.example.data.models.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DiscipleDataController {


    private final DiscipleService discipleService;
    private final ModelMapperService mapperService;


    @PostMapping(path = {"/disciple/save/", "/disciple/save"})
    public ResponseEntity<ApiResponse<DiscipleDTO>> save(
            @RequestBody ApiRequest<DiscipleDTO> request
    ) {

        Disciple disciple = mapperService.toEntity(request.getData(), Disciple.class);


        return ResponseEntity.ok().body(discipleService.save(disciple));
    }


    @PostMapping(path = {"/disciple/update/", "/disciple/update"})
    public ResponseEntity<ApiResponse<DiscipleDTO>> update(
            @RequestBody ApiRequest<DiscipleDTO> request
    ) {
        Disciple disciple = mapperService.toEntity(request.getData(), Disciple.class);


        return ResponseEntity.ok().body(discipleService.update(disciple));
    }


    @GetMapping("/disciple/{id}")
    public ResponseEntity<ApiResponse<DiscipleDTO>> getById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok().body(discipleService.getById(id));
    }


    @GetMapping("/disciple/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> existsById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok().body(discipleService.existsById(id));
    }

    @GetMapping("/disciple/exists/curator_id/{id}")
    public ResponseEntity<ApiResponse<Boolean>> existsByCuratorId(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok().body(discipleService.existsByCuratorId(id));
    }

    @PostMapping("/disciple/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok().body(discipleService.delete(id));
    }


}