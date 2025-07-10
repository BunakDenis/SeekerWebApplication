package com.example.utils.text.cheker;

import dumonts.hunspell.Hunspell;
import lombok.extern.log4j.Log4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
@Log4j
public class HunspellSpellChecker implements SpellChecker {

    private final Hunspell hunspell;

    public HunspellSpellChecker() {

        try {
            ClassPathResource resourceAff = new ClassPathResource("dictionaries/ru_RU.aff");
            ClassPathResource resourceDic = new ClassPathResource("dictionaries/ru_RU.dic");

            File modelFileAff = resourceAff.getFile();
            File modelFileDic = resourceDic.getFile();

            Path pathAffFile = Paths.get(modelFileAff.getAbsolutePath());
            Path pathDicFile = Paths.get(modelFileDic.getAbsolutePath());

            this.hunspell = new Hunspell(pathAffFile, pathDicFile);

            log.debug("Hunspell init successfully!");

        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить словари Hunspell", e);
        }
    }

    @Override
    public String check(String inputText) {
        StringBuilder corrected = new StringBuilder();
        for (String word : inputText.split("\\s+")) {
            if (!hunspell.spell(word)) {
                log.debug("Проверяемое на орфографию слово - " + word);

                String[] suggest = hunspell.suggest(word);
                List<String> suggestions = Arrays.asList(suggest);

                log.debug(suggestions);

                corrected.append(suggestions.isEmpty() ? word : suggestions.get(0));
            } else {
                corrected.append(word);
            }
            corrected.append(" ");
        }
        return corrected.toString().trim();
    }

    private Path extractResourceToTempFile(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new FileNotFoundException("Ресурс не найден: " + resourcePath);
        }

        Path tempFile = Files.createTempFile("hunspell_", "_" + Paths.get(resourcePath).getFileName());
        tempFile.toFile().deleteOnExit();

        try (OutputStream outputStream = Files.newOutputStream(tempFile)) {
            inputStream.transferTo(outputStream);
        }

        return tempFile;
    }

}