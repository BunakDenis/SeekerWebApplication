package com.example.utils.text.formatter;


import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.log4j.Log4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

@Component
@Log4j
public class CoreNlpTextFormatter implements TextFormatter {
    private final StanfordCoreNLP pipeline;
    public CoreNlpTextFormatter() {
        String modelPath = "models/coreNLP/stanford-russian-corenlp-models.jar";
        URL jarURL = null;

        try {
            ClassPathResource jarFile = new ClassPathResource(modelPath);

            File modelFile = jarFile.getFile();

            jarURL = modelFile.toURI().toURL();

        } catch (MalformedURLException e) {
            log.error("MalformedURLException - " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("File by path " + modelPath + " not found ");
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL}, Thread.currentThread().getContextClassLoader());

        // Устанавливаем classloader для текущего потока
        Thread.currentThread().setContextClassLoader(classLoader);

        // Настройка пайплайна
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit");

        /*
        props.setProperty("tokenize.language", "ru");
        */

        props.setProperty("pipelineLanguage", "ru");

        this.pipeline = new StanfordCoreNLP(props);

        log.debug("Core NLP init successfully!");
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
