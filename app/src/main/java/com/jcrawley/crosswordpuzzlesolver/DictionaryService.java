package com.jcrawley.crosswordpuzzlesolver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DictionaryService extends Service {

    IBinder binder = new LocalBinder();
    private DictionaryLoader dictionaryLoader;
    private WordSearcher wordSearcher;
    private final AnagramFinder anagramFinder = new AnagramFinder();
    private final AtomicBoolean isSearchRunning = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isDictionaryLoaded = new AtomicBoolean(false);


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
        waitForDictionaryToLoad();
        ifNotSearching(()->{
            String formattedInput = inputText.trim().toLowerCase();
            if (formattedInput.isEmpty()) {
                wordListView.setWords(Collections.emptyList());
            }
            var initialResults = getInitialResultsFor(formattedInput, isUsingAnagrams);
            List<String> results = new ArrayList<>(excludeWordsWithDisallowedLetters(initialResults, excludedLettersStr));
            wordListView.setWords(results);
        });
    }


    public void getResultsForPattern(String pattern, WordListView wordListView){
        waitForDictionaryToLoad();
        ifNotSearching(()->{
            List<String> results = wordSearcher.searchForPattern(pattern);
            wordListView.setWords(results);
        });
    }


    public void findWords(String input, String requiredLetters, WordListView wordListView){
        waitForDictionaryToLoad();
        ifNotSearching(()-> wordListView.setWords(anagramFinder.getWordsFrom(input, requiredLetters)));
    }
    

    public boolean doesWordExist(String word){
        waitForDictionaryToLoad();
        var wordsMap = dictionaryLoader.getWordsMap();
        String sortedWord = getSortedWord(word);
        if(wordsMap.containsKey(sortedWord)){
            Set<String> words = wordsMap.get(sortedWord);
            return words != null && words.contains(word);
        }
        return false;
    }


    private void waitForDictionaryToLoad(){
        try{
            dictionaryLoader.getDictionaryLatch().await();
        }catch (InterruptedException e){
            handleException(e);
        }

    }


    private void handleException(Exception e){
       var message = e.getMessage();
       log(message);
    }


    private void ifNotSearching(Runnable runnable) {
        if(isSearchRunning.get()){
            return;
        }
        isSearchRunning.set(true);
        Runnable task = () ->{
            runnable.run();
            isSearchRunning.set(false);
        };
        executor.submit(task);
    }


    private void log(String msg){
        System.out.println("^^^ DictionaryService: " + msg);
    }


    private List<String> excludeWordsWithDisallowedLetters(List<String> initialResults, String excludedLettersStr){
        if(excludedLettersStr.isEmpty()){
            return new ArrayList<>(initialResults);
        }
        List<String> excludedLetters = Arrays.asList(excludedLettersStr.split(""));
        return createListOfAllowedWords(initialResults, excludedLetters);
    }


    private List<String> createListOfAllowedWords(List<String> inputList, List<String> excludedLetters){
        return inputList.stream()
                .filter(word -> isWordFreeOfExcludedLetters(word, excludedLetters))
                .collect(Collectors.toList());
    }


    private boolean isWordFreeOfExcludedLetters(String word, List<String> excludedLetters){
        String lowercaseWord = word.toLowerCase();
        return excludedLetters.stream()
                .noneMatch(lowercaseWord::contains);
    }


    private List<String> getInitialResultsFor(String inputText, boolean isUsingAnagrams){
        var words = isUsingAnagrams ? anagramFinder.getWordsMatching(inputText)
                : wordSearcher.searchFor(inputText);

        return new ArrayList<>(words);
    }


    private void loadDictionaryWords(){
        Executors.newSingleThreadExecutor().submit(
                ()-> {
                    isDictionaryLoaded.set(false);
                    dictionaryLoader = new DictionaryLoaderImpl(getApplicationContext());
                    dictionaryLoader.loadDictionary();
                    log("loadDictionaryWords() number of words = " + dictionaryLoader.getWordCount());
                    wordSearcher = new WordSearcher(dictionaryLoader.getWordsList(), dictionaryLoader.getWordsStr());
                    anagramFinder.setupWordsMap(dictionaryLoader.getWordsMap());
                    anagramFinder.setWordsByLengthMap(dictionaryLoader.getWordsByLengthMap());
                    isDictionaryLoaded.set(true);
                } );
    }


    private String getSortedWord(String word){
        return Arrays.stream(word.split("")).sorted().collect(Collectors.joining(""));
    }


    @Override
    public IBinder onBind(Intent intent) {
        if(!isDictionaryLoaded.get()){
            loadDictionaryWords();
        }
        return binder;
    }


    public class LocalBinder extends Binder {
        public DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}