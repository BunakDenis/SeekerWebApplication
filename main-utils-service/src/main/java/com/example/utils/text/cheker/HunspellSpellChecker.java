package com.example.utils.text.cheker;

import dumonts.hunspell.Hunspell;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;


import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Component
@Log4j
public class HunspellSpellChecker implements SpellChecker {
    private final Hunspell hunspell;

    public HunspellSpellChecker() {
        Path addFilePath = Path.of("./src/main/resources/dictionaries/ru_RU.aff");
        Path dicFilePath = Path.of("./src/main/resources/dictionaries/ru_RU.dic");

        this.hunspell = new Hunspell(
                addFilePath,
                dicFilePath
        );
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
}