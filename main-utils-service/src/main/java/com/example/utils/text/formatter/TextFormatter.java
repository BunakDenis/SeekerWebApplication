package com.example.utils.text.formatter;

public interface TextFormatter {
    /**
     * Добавляет пунктуацию и разбивает на абзацы.
     * @param inputText текст без пунктуации
     * @return отформатированный текст
     */
    String format(String inputText);
}
