package com.example.utils.text.cheker;

public interface SpellChecker {
    /**
     * Проверяет и исправляет орфографию текста.
     * @param inputText исходный текст
     * @return исправленный текст
     */
    String check(String inputText);
}
