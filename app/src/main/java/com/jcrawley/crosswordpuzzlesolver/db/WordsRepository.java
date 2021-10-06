package com.jcrawley.crosswordpuzzlesolver.db;

import java.util.List;

public interface WordsRepository {

    void addWord(String word, String key);

    List<String> getWords(String key);

}
