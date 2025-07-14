package com.example.tools.text.formatter;


import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

@Component
@Log4j2
public class CoreNlpTextFormatter implements TextFormatter {
    private final StanfordCoreNLP pipeline;
    public CoreNlpTextFormatter() {
        String modelPath = "models/coreNLP/stanford-russian-corenlp-models.jar";
        URL jarURL;

        try {
            // Извлекаем JAR из classpath во временный файл
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(modelPath);

            if (inputStream == null) {
                throw new FileNotFoundException("Не удалось найти модель в ресурсах: " + modelPath);
            }

            // Создаём временный файл
            File tempJarFile = File.createTempFile("coreNLP-model", ".jar");
            tempJarFile.deleteOnExit();

            try (OutputStream out = new FileOutputStream(tempJarFile)) {
                inputStream.transferTo(out);
            }

            jarURL = tempJarFile.toURI().toURL();

        } catch (IOException e) {
            log.error("Ошибка загрузки модели из ресурсов: " + modelPath, e);
            throw new RuntimeException("Не удалось загрузить модель CoreNLP", e);
        }

        // Загружаем JAR через classloader
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL}, Thread.currentThread().getContextClassLoader());

        // Устанавливаем classloader
        Thread.currentThread().setContextClassLoader(classLoader);

        // Конфигурация пайплайна
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit");
        props.setProperty("pipelineLanguage", "ru");

        this.pipeline = new StanfordCoreNLP(props);

        log.debug("Core NLP инициализирован успешно!");
    }

    @Override
    public String format(String inputText) {
        CoreDocument doc = new CoreDocument(inputText);
        pipeline.annotate(doc);

        for (CoreSentence sentence : doc.sentences()) {
            System.out.println("→ " + sentence.text());
        }
        return doc.toString();
    }
}
