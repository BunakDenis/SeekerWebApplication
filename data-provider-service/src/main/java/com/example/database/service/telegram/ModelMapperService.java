package com.example.database.service.telegram;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

}
