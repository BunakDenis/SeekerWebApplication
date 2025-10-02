package com.example.database.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ModelMapperService {

    private final ModelMapper mapper;

    public <T, K> T toEntity (K dto, Class<T> entity) {
        return mapper.map(dto, entity);
    }

    public <T, K> K toDTO (T entity, Class<K> dto) {
        return mapper.map(entity, dto);
    }

    public <T, K> List<K> dtoListToEntity(List<T> dtoList, Class<K> entityClass) {

        if (dtoList.isEmpty()) return Collections.emptyList();

        List<K> result = new ArrayList<>();

        dtoList.forEach(dto -> result.add(mapper.map(dto, entityClass)));

        return List.copyOf(result);

    }

    public <T, K> List<K> entityListToDto(List<T> entityList, Class<K> entityClass) {

        if (entityList.isEmpty()) return Collections.emptyList();

        List<K> result = new ArrayList<>();

        entityList.forEach(dto -> result.add(mapper.map(dto, entityClass)));

        return List.copyOf(result);

    }

}
