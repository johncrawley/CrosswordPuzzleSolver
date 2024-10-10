package com.jcrawley.crosswordpuzzlesolver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DictionaryService extends Service {

    IBinder binder = new LocalBinder();
    private MainActivity mainActivity;
    private DictionaryLoader dictionaryLoader;
    private WordSearcher wordSearcher;
    private final AnagramFinder anagramFinder = new AnagramFinder();
    private final AtomicBoolean isSearchRunning = new AtomicBoolean(false);


    public DictionaryService() {
        loadDictionaryWords();
    }

    public AnagramFinder getAnagramFinder(){
        return anagramFinder;
    }


    public WordSearcher getWordSearcher(){
        return wordSearcher;
    }


    public void runPuzzleHelperSearch(String inputText, String excludedLettersStr, boolean isUsingAnagrams, WordListView wordListView){
        ifNotSearching(()->{
            String formattedInput = inputText.trim().toLowerCase();
            if (formattedInput.isEmpty()) {
                wordListView.setWords(Collections.emptyList());
            }
            var initialResults = getInitialResultsFor(formattedInput, isUsingAnagrams);
            List<String> results = new ArrayList<>(excludeWordsWithBanishedLetters(initialResults, excludedLettersStr));
            wordListView.setWords(results);
        });
    }


    public void getResultsForPattern(String pattern, WordListView wordListView){
        ifNotSearching(()->{
            List<String> results = wordSearcher.searchForPattern(pattern);
            wordListView.setWords(results);
        });
    }


    private void ifNotSearching(Runnable runnable) {
        if(isSearchRunning.get()){
            return;
        }
        isSearchRunning.set(true);
        runnable.run();
        isSearchRunning.set(false);
    }


    private void log(String msg){
        System.out.println("^^^ DictionaryService: " + msg);
    }


    private List<String> excludeWordsWithBanishedLetters(List<String> initialResults, String excludedLettersStr){
        if(excludedLettersStr.isEmpty()){
            return new ArrayList<>(initialResults);
        }
        List<String> excludedLetters = Arrays.asList(excludedLettersStr.split(""));
        return createListOfAllowedWords(initialResults, excludedLetters);
    }


    private List<String> createListOfAllowedWords(List<String> inputList, List<String> excludedLetters){
        return inputList.stream().filter(word -> isWordFreeOfExcludedLetters(word, excludedLetters)).collect(Collectors.toList());
    }


    private boolean isWordFreeOfExcludedLetters(String word, List<String> excludedLetters){
        String lowercaseWord = word.toLowerCase();
        return excludedLetters.stream().noneMatch(lowercaseWord::contains);
    }


    private List<String> getInitialResultsFor(String inputText, boolean isUsingAnagrams){
        var words = isUsingAnagrams ?
                anagramFinder.getWordsMatching(inputText) : wordSearcher.searchFor(inputText);

        return new ArrayList<>(words);
    }


    private void loadDictionaryWords(){
        Executors.newSingleThreadExecutor().submit(
                ()-> {
                    dictionaryLoader = new DictionaryLoaderImpl(getApplicationContext());
                    dictionaryLoader.retrieveAllWords();
                    wordSearcher = new WordSearcher(dictionaryLoader.getWordsList(), dictionaryLoader.getWordsStr());
                    anagramFinder.setupWordsMap(dictionaryLoader.getWordsMap());
                    anagramFinder.setWordsByLengthMap(dictionaryLoader.getWordsByLengthMap());
                } );
    }


    public DictionaryLoader getDictionaryLoader(){
        return dictionaryLoader;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void setActivity(MainActivity activity){
        this.mainActivity = activity;
    }


    public class LocalBinder extends Binder {
        public DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}