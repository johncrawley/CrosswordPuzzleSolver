package com.jcrawley.crosswordpuzzlesolver.dictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface DictionaryLoader {

    void retrieveAllWords();
    void loadWordsIntoDb(Consumer<String> lineConsumer);
    String getWordsStr();
    Map<String, Set<String>> getWordsMap();
    Map<Integer, Map<String, Set<String>>> getWordsByLengthMap();
    List<String> getWordsList();
    int getWordCount();

}
