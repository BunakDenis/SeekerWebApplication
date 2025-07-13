package com.example.tools.audio.decoder;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j
public class VoskResult {

    private String text;

    public String getSerializeText() {
        Gson gson = new Gson();

        String sourceText = gson.fromJson(this.text, VoskResult.class).getText();

        return fixEncoding(sourceText);
    }

    private static String fixEncoding(String garbledText) {
        try {
            byte[] bytes = garbledText.getBytes("Windows-1251"); // как будто строка была в win-1251
            return new String(bytes, "UTF-8");                   // а на самом деле она в UTF-8
        } catch (UnsupportedEncodingException e) {
            log.error("Ошибка изменения кодировки текста " + e.getMessage(), e);
            return garbledText;
        }
    }

}
