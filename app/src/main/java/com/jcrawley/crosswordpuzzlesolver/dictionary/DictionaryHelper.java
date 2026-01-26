package com.jcrawley.crosswordpuzzlesolver.dictionary;

import com.jcrawley.crosswordpuzzlesolver.WordListView;

import java.util.List;

public interface DictionaryHelper {

    boolean isDictionaryLoaded();
    void init(Dictionary dictionary);
    List<String> runPuzzleHelperSearch(String inputText, String excludedLettersStr, boolean isUsingAnagrams, WordListView wordListView);
    List<String> getResultsForPattern(String pattern, WordListView wordListView);
    void findWords(String input, String requiredLetters, WordListView wordListView);
    boolean doesWordExist(String word);
    boolean isNotCurrentlySearching();
}
