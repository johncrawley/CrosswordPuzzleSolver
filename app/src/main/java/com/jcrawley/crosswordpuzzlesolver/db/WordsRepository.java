package com.jcrawley.crosswordpuzzlesolver.db;

import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;

import java.util.List;
import java.util.Set;

public interface WordsRepository {

    void saveWordsFromMapFile();
    void addWord(String word, String key);
    List<String> getWords(String key);
    Set<String> findWordsWithKey(String key);
    Set<String> getAllWords();
    boolean hasAnyWords();
    void saveWordsFromDictionary(DictionaryLoader dictionaryLoader);


}
