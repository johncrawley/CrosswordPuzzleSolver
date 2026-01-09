package com.jcrawley.crosswordpuzzlesolver.dictionary;

import com.jcrawley.crosswordpuzzlesolver.WordListView;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DictionaryHelperImpl implements DictionaryHelper{

    private WordSearcher wordSearcher;
    private final AnagramFinder anagramFinder = new AnagramFinder();
    private final AtomicBoolean isSearchRunning = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isDictionaryLoaded = new AtomicBoolean(false);


    @Override
    public boolean isDictionaryLoaded() {
        return false;
    }


    @Override
    public void init(Dictionary dictionary){
        isDictionaryLoaded.set(false);
        wordSearcher = new WordSearcher(dictionary.wordsList(), dictionary.wordsStr());
        anagramFinder.setupWordsMap(dictionary.wordsMap());
        anagramFinder.setWordsByLengthMap(dictionary.wordsByLengthMap());
        isDictionaryLoaded.set(true);
    }


    @Override
    public void setWords(List<String> words) {

    }

    @Override
    public void runPuzzleHelperSearch(String inputText, String excludedLettersStr, boolean isUsingAnagrams, WordListView wordListView) {

    }

    @Override
    public void getResultsForPattern(String pattern, WordListView wordListView) {

    }

    @Override
    public void findWords(String input, String requiredLetters, WordListView wordListView) {

    }

    @Override
    public boolean doesWordExist(String word) {
        return false;
    }
}
