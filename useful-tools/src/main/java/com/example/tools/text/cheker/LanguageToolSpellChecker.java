package com.example.tools.text.cheker;

import lombok.extern.log4j.Log4j;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Russian;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Log4j
public class LanguageToolSpellChecker implements SpellChecker {
    private final JLanguageTool langTool;

    public LanguageToolSpellChecker() {
        this.langTool = new JLanguageTool(new Russian());
    }

    @Override
    public String check(String inputText) {
        try {
            // --- 1. Инициализация ---
            JLanguageTool langTool = new JLanguageTool(new Russian());

            // --- 2. Проверка текста на ошибки ---
            List<RuleMatch> matches = langTool.check(inputText);

            // --- 3. Автоисправление ---
            StringBuilder correctedText = new StringBuilder();
            int lastPos = 0;

            for (RuleMatch match : matches) {
                int from = match.getFromPos();
                int to = match.getToPos();

                // Добавить текст до ошибки
                correctedText.append(inputText, lastPos, from);

                // Добавить исправление, если есть
                List<String> replacements = match.getSuggestedReplacements();
                if (!replacements.isEmpty()) {
                    correctedText.append(replacements.get(0));
                } else {
                    correctedText.append(inputText, from, to); // оставить как есть
                }

                lastPos = to;
            }

            // Добавить остаток текста после последней ошибки
            correctedText.append(inputText.substring(lastPos));

            return correctedText.toString();
        } catch (IOException e) {
            log.error("Error checking text: {}" + e.getMessage(), e);
            // Return original text if error occurs
            return inputText;
        }
    }
}