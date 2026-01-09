package com.jcrawley.crosswordpuzzlesolver.dictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public interface DictionaryLoader {

    Dictionary loadDictionary();
    void loadWordsIntoDb(Consumer<String> lineConsumer);
    String getWordsStr();
    Map<String, Set<String>> getWordsMap();
    Map<Integer, Map<String, Set<String>>> getWordsByLengthMap();
    List<String> getWordsList();
    int getWordCount();
    CountDownLatch getDictionaryLatch();

}
