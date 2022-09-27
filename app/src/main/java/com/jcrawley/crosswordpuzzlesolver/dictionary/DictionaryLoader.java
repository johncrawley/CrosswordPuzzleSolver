package com.jcrawley.crosswordpuzzlesolver.dictionary;

import java.util.function.Consumer;

public interface DictionaryLoader {

    String retrieveAllWords();
    void loadWordsIntoDb(Consumer<String> lineConsumer);
}
