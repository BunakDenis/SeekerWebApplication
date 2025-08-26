package com.example.utils.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class EmailValidator implements Validator {

    private final ObjectMapper objectMapper;

    @Override
    public boolean isValid(Object object) {

        String email = objectMapper.convertValue(object, String.class);

        return false;
    }
}
