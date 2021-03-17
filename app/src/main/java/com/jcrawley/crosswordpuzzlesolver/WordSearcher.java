package com.jcrawley.crosswordpuzzlesolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordSearcher {

    private String words;


    public WordSearcher(String words){
        this.words = words;
    }


    List<String> searchFor(String searchText){
        final String WORD_SEPARATOR = "\\b";

        String inputText = WORD_SEPARATOR + searchText + WORD_SEPARATOR;
        Pattern pattern = Pattern.compile(inputText);
        return getMatchingWords(pattern);
    }


    private List<String> getMatchingWords(Pattern tagMatcher) {
        Set<String> foundWords = new HashSet<>();
        Matcher matcher = tagMatcher.matcher(this.words);
        while(matcher.find()) {
            String result = matcher.group();
            result = result.trim();
            if(!result.contains(" ")){
                foundWords.add(result);
            }
        }
        return new ArrayList<>(foundWords);
    }


}
