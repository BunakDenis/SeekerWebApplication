package com.example.database;

import com.example.database.DataProviderService;
import com.example.utils.file.loader.EnvLoader;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = DataProviderService.class)
@AutoConfigureMockMvc
@Slf4j
public class DataProviderTests {

    @Autowired
    private MockMvc mockMvc;

    private static Dotenv dotenv;

    static {
        dotenv = EnvLoader.DOTENV;
    }
/*
    @Test
    public void actuatorTests() throws Exception {
        String response = mockMvc.perform(get("/actuator/health"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.debug(response);
    }
*/
}
