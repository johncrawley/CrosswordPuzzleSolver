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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Character, Integer> requiredLettersMap;


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


    public void findWords(String input, String requiredLetters, WordListView wordListView){
        ifNotSearching(()->{
            String completeInput = input + requiredLetters;
            if(completeInput.isEmpty()){
                wordListView.setWords(Collections.emptyList());
            }
            initRequiredLettersMap(requiredLetters);
            List<String> results = filterResultsWithRequiredLetters(anagramFinder.getWordsFrom(completeInput), requiredLetters);

            wordListView.setWords(filterResultsWithMultipleLetters(results));
        });
    }


    private void initRequiredLettersMap(String requiredLetters){
        requiredLettersMap = new HashMap<>();
        for(int i = 0; i < requiredLetters.length(); i++){
            requiredLettersMap.merge(requiredLetters.charAt(i), 1, Integer::sum);
        }
    }


    private List<String> filterResultsWithMultipleLetters(List<String> results){
        return results.stream().filter(this::hasAllRequiredChars).collect(Collectors.toList());
    }


    private boolean hasAllRequiredChars(String results){
        if(requiredLettersMap.isEmpty()){
            return true;
        }
        for(char ch : requiredLettersMap.keySet()){
            Integer requiredNumber = requiredLettersMap.get(ch);
            if(requiredNumber == null || !isContainingAmount(results, ch, requiredNumber)){
                return false;
            }
        }
        return true;
    }


    private boolean isContainingAmount(String str, char charVal, int expectedAmount){
        return str.chars().filter(ch -> ch == charVal).count() >= expectedAmount;
    }


    private List<String> filterResultsWithRequiredLetters(List<String> words, String requiredLetters){
        List<String> requiredLettersList = createRequiredLettersList(requiredLetters);
        if(requiredLetters.isEmpty()){
            return words;
        }
        return words.stream().filter(word -> doesWordHaveAllLetters(word, requiredLettersList)).collect(Collectors.toList());
    }


    private List<String> createRequiredLettersList(String requiredLetters){
        String azRequiredLetters = requiredLetters.replaceAll("[^A-za-z]", "");
        return azRequiredLetters.isEmpty() ? Collections.emptyList() : Arrays.asList(azRequiredLetters.split(""));
    }


    private boolean doesWordHaveAllLetters(String word, List<String> letters){
        return letters.stream().allMatch(word::contains);
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


    public boolean doesWordExist(String word){
        var wordsMap = dictionaryLoader.getWordsMap();
        String sortedWord = getSortedWord(word);
        if(wordsMap.containsKey(sortedWord)){
            Set<String> words = wordsMap.get(sortedWord);
            return words != null && words.contains(word);
        }
        return false;
    }


    private String getSortedWord(String word){
        return Arrays.stream(word.split("")).sorted().collect(Collectors.joining(""));
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class LocalBinder extends Binder {
        public DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}