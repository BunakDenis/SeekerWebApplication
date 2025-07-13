package com.example.tools.audio.decoder;

public interface AudioDecoder {
    /**
     * Распознаёт речь из аудиофайла и возвращает текст.
     * @param audioPath путь к аудиофайлу
     * @return распознанный текст
     */
    String decode(String audioPath);
}
