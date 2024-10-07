package com.jcrawley.crosswordpuzzlesolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordSearcher {

    private List<String> badCharacters;
    private final HashSet<String> foundWords;
    private int expectedCharacters;
    private final List<String> wordsList;
    private final String wordsStr;


    public WordSearcher(List<String> wordsList, String wordsStr){
        boolean isWordsStrNull = wordsStr == null;
        log("entered WordSearcher, wordsStr is null: " + isWordsStrNull);
        this.wordsList = wordsList;
        this.wordsStr = wordsStr;
        setupBadCharacters();
        foundWords = new HashSet<>();
    }

    private void setupBadCharacters(){
        badCharacters = Arrays.asList(" ", "'");
    }


    public List<String> searchFor(String searchText){
        log("Entered searchFor("  + searchText + ")");
       // log("wordsStr length: " + wordsStr.length());
        log("size of words list: " + wordsList.size());
        final String WORD_SEPARATOR = "\\b";
        expectedCharacters = searchText.length();
        String inputText = WORD_SEPARATOR + searchText + WORD_SEPARATOR;
        Pattern pattern = Pattern.compile(inputText);
        return getMatchingWords(pattern);
    }


    private void log(String msg){
        System.out.println("^^^ WordSearcher: " + msg);
    }


    public List<String> searchForPattern(String patternStr){
        Pattern tagMatcher = Pattern.compile(patternStr);
        return wordsList.parallelStream()
                .filter(word -> tagMatcher.matcher(word).find())
                        .collect(Collectors.toList());
    }


    /*
        It's quicker to search for a pattern with all the dictionary words in a single string,
         than iterating through a list of words and running the pattern against each item
            using single string: 15, 335, 373, 343 millis,
            using list of words: 824,927, 862, 961  millis
     */
    private List<String> getMatchingWords(Pattern tagMatcher) {
        foundWords.clear();
        if(wordsStr == null){
            return Collections.emptyList();
        }
        processResults(tagMatcher);
        List<String> results = new ArrayList<>(foundWords);
        Collections.sort(results);
        return results;
    }


    private void processResults(Pattern tagMatcher){
        Matcher matcher = tagMatcher.matcher(wordsStr);
        while(matcher.find()) {
            processResult(matcher.group());
        }
    }


    private void processResult(String result){
        String word = result.trim();
        if(hasValidCharactersOnly(word) && isCorrectLength(word)){
            foundWords.add(word);
        }
    }


    private boolean hasValidCharactersOnly(String word){
        for(String badCharacter:  badCharacters){
            if(word.contains(badCharacter)){
                return false;
            }
        }
        return true;
    }


    private boolean isCorrectLength(String word){
        return word.length() == expectedCharacters;
    }


}
