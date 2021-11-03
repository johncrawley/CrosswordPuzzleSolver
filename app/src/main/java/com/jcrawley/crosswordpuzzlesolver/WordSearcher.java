package com.jcrawley.crosswordpuzzlesolver;

import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordSearcher {

    private List<String> badCharacters;
    private final HashSet<String> foundWords;
    private int expectedCharacters;
    private final MainViewModel viewModel;

    public WordSearcher(MainViewModel viewModel){
        this.viewModel = viewModel;
        setupBadCharacters();
        foundWords = new HashSet<>();
    }

    private void setupBadCharacters(){
        badCharacters = Arrays.asList(" ", "'");
    }


    public List<String> searchFor(String searchText){
        final String WORD_SEPARATOR = "\\b";
        expectedCharacters = searchText.length();
        String inputText = WORD_SEPARATOR + searchText + WORD_SEPARATOR;
        Pattern pattern = Pattern.compile(inputText);
        return getMatchingWords(pattern);
    }


    private List<String> getMatchingWords(Pattern tagMatcher) {
        foundWords.clear();
        if(viewModel.wordsStr == null){
            return Collections.emptyList();
        }
        Matcher matcher = tagMatcher.matcher(viewModel.wordsStr);
        while(matcher.find()) {
            processResult(matcher.group());
        }
        List<String> results = new ArrayList<>(foundWords);
        Collections.sort(results);
        return results;
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
