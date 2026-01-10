package com.jcrawley.crosswordpuzzlesolver.dictionary;

import com.jcrawley.crosswordpuzzlesolver.WordListView;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DictionaryHelperImpl implements DictionaryHelper{

    private WordSearcher wordSearcher;
    private final AnagramFinder anagramFinder = new AnagramFinder();
    private final AtomicBoolean isSearchRunning = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isDictionaryLoaded = new AtomicBoolean(false);
    private CountDownLatch dictionaryLatch;
    private Map<String, Set<String>> wordsMap;


    public DictionaryHelperImpl(){
        dictionaryLatch = new CountDownLatch(1);
    }


    @Override
    public boolean isDictionaryLoaded() {
        return false;
    }


    @Override
    public void init(Dictionary dictionary){
        isDictionaryLoaded.set(false);
        dictionaryLatch = new CountDownLatch(1);
        wordsMap = dictionary.wordsMap();
        wordSearcher = new WordSearcher(dictionary.wordsList(), dictionary.wordsStr());
        anagramFinder.setupWordsMap(wordsMap);
        anagramFinder.setWordsByLengthMap(dictionary.wordsByLengthMap());
        isDictionaryLoaded.set(true);
        dictionaryLatch.countDown();
    }


    @Override
    public void setWords(List<String> words) {

    }


    @Override
    public void runPuzzleHelperSearch(String inputText, String excludedLettersStr, boolean isUsingAnagrams, WordListView wordListView) {
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


    @Override
    public void getResultsForPattern(String pattern, WordListView wordListView) {
        waitForDictionaryToLoad();
        ifNotSearching(()->{
            List<String> results = wordSearcher.searchForPattern(pattern);
            wordListView.setWords(results);
        });
    }


    @Override
    public void findWords(String input, String requiredLetters, WordListView wordListView) {
        waitForDictionaryToLoad();
        ifNotSearching(()-> wordListView.setWords(anagramFinder.getWordsFrom(input, requiredLetters)));
    }


    @Override
    public boolean doesWordExist(String word) {
        waitForDictionaryToLoad();
        String sortedWord = getSortedWord(word);
        if(wordsMap.containsKey(sortedWord)){
            Set<String> words = wordsMap.get(sortedWord);
            return words != null && words.contains(word);
        }
        return false;
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


    private void waitForDictionaryToLoad(){
        try{
            dictionaryLatch.await();
        }catch (InterruptedException e){
            handleException(e);
        }

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

    private void handleException(Exception e){
        var message = e.getMessage();
        log(message);
    }


    private String getSortedWord(String word){
        return Arrays.stream(word.split(""))
                .sorted()
                .collect(Collectors.joining(""));
    }


    private void log(String msg){
        System.out.println("^^^ DictionaryHelperImpl: " + msg);
    }

}
