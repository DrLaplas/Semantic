package org.example;

import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.postag.*;
import opennlp.tools.lemmatizer.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AnalyzeRequests {

    private static final String MODELS_PATH = "src/main/resources/models/";

    private static final Set<String> getVerbs = Set.of(
            "запросить", "получать", "брать", "скачать", "считать",
            "просматривать", "запрашивать", "выгрузить", "читать", "найти", "сохранять"
    );

//    private static final Map<String, String> verbLemmas = Map.ofEntries(
//            Map.entry("запрашивает", "запросить"),
//            Map.entry("запрашивал", "запросить"),
//            Map.entry("запросил", "запросить"),
//            Map.entry("отправил", "отправить"),
//            Map.entry("отправляет", "отправить"),
//            Map.entry("отправлял", "отправить"),
//            Map.entry("получает", "получить"),
//            Map.entry("получил", "получить"),
//            Map.entry("добавил", "добавить"),
//            Map.entry("добавляет", "добавить"),
//            Map.entry("обновил", "обновить"),
//            Map.entry("обновляет", "обновить"),
//            Map.entry("удалил", "удалить"),
//            Map.entry("удаляет", "удалить"),
//            Map.entry("загрузил", "загрузить"),
//            Map.entry("загружает", "загрузить"),
//            Map.entry("передал", "передать"),
//            Map.entry("передаёт", "передать"),
//            Map.entry("передает", "передать"),
//            Map.entry("сохранил", "сохранить"),
//            Map.entry("сохраняет", "сохранить"),
//            Map.entry("выгрузил", "выгрузить"),
//            Map.entry("выгружает", "выгрузить"),
//            Map.entry("просматривает", "просматривать"),
//            Map.entry("создаёт", "создать"),
//            Map.entry("создает", "создать"),
//            Map.entry("создал", "создать"),
//            Map.entry("читает", "читать"),
//            Map.entry("прочитал", "читать"),
//            Map.entry("находит", "найти"),
//            Map.entry("нашёл", "найти"),
//            Map.entry("нашел", "найти")
//    );

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("file.encoding"));

        List<String> sentences = List.of(
                "пользователь получает пиву",
                "пиво закончилось",
                "Пользователь сохраняет кегу"
        );

        for (String sentence : sentences) {
            String[] tokens = tokenize(sentence);
            String[] posTags = tagPOS(tokens);
            //String[] lemmas = lemmatizeWithDictionary(tokens);
            String[] lemmas = lemmatize(tokens, posTags);

            String requestType = detectRequestType(lemmas);

            System.out.println("Предложение: " + sentence);
            System.out.println("Токены: " + Arrays.toString(tokens));
            System.out.println("POS: " + Arrays.toString(posTags));
            System.out.println("Леммы: " + Arrays.toString(lemmas));
            System.out.println("Тип запроса: " + requestType);
            System.out.println("-".repeat(50));
        }
    }

    private static String[] tokenize(String sentence) throws IOException {
        try (InputStream modelIn = Files.newInputStream(Paths.get(MODELS_PATH + "opennlp-ru-ud-gsd-tokens-1.3-2.5.4.bin"))) {
            TokenizerModel model = new TokenizerModel(modelIn);
            TokenizerME tokenizer = new TokenizerME(model);
            return tokenizer.tokenize(sentence);
        }
    }

    private static String[] tagPOS(String[] tokens) throws IOException {
        try (InputStream modelIn = Files.newInputStream(Paths.get(MODELS_PATH + "opennlp-ru-ud-gsd-pos-1.3-2.5.4.bin"))) {
            POSModel model = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(model);
            return tagger.tag(tokens);
        }
    }

    private static String[] lemmatize(String[] tokens, String[] posTags) throws IOException {
        try (InputStream modelIn = Files.newInputStream(Paths.get(MODELS_PATH + "opennlp-ru-ud-gsd-lemmas-1.3-2.5.4.bin"))) {
            LemmatizerModel model = new LemmatizerModel(modelIn);
            LemmatizerME lemmatizer = new LemmatizerME(model);
            return lemmatizer.lemmatize(tokens, posTags);
        }
    }

//    private static String[] tokenize(String sentence) {
//        return sentence.split("\\s+");
//    }
//
//    private static String[] tagPOS(String[] tokens) {
//        String[] tags = new String[tokens.length];
//        Arrays.fill(tags, "NOUN");
//        return tags;
//    }

//    private static String[] lemmatizeWithDictionary(String[] tokens) {
//        System.out.println("=== ВЫЗВАН lemmatizeWithDictionary ===");
//        String[] result = new String[tokens.length];
//        for (int i = 0; i < tokens.length; i++) {
//            String token = tokens[i].toLowerCase();
//            result[i] = verbLemmas.getOrDefault(token, token);
//        }
//        return result;
//    }


    private static String detectRequestType(String[] lemmas) {
        for (String lemma : lemmas) {
            if (getVerbs.contains(lemma)) {
                return "GET";
            }
        }
        return "UNKNOWN";
    }
}
