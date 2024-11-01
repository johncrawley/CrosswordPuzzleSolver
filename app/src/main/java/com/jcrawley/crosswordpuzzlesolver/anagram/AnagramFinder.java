package com.jcrawley.crosswordpuzzlesolver.anagram;


import com.jcrawley.crosswordpuzzlesolver.db.WordsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnagramFinder {

    private  BinaryCounter binaryCounter;
    private  WordsRepository wordsRepository;
    private  Map<String, Set<String>> wordsMap;
    private  Map<Integer, Map<String, Set<String>>> wordsByLengthMap;

    public AnagramFinder(){

    }


    public void setupWordsMap( Map<String, Set<String>> wordsMap){
        this.wordsMap = wordsMap;
    }


    public void setWordsByLengthMap(Map<Integer, Map<String, Set<String>>> wordsByLengthMap){
        this.wordsByLengthMap = wordsByLengthMap;
    }


    public List<String> getWordsFrom(String providedLetters){
        Set<String> foundWords = new HashSet<>();
        String sortedLetters= sort(providedLetters);
        binaryCounter = new BinaryCounter(sortedLetters.length());

        while(!binaryCounter.isIndexAtLimit()){
            addWordsTo(foundWords, sortedLetters);
            //addWordsFromRepositoryTo(foundWords, sortedLetters);
        }
        return getSortedListOf(foundWords);
    }


    // used to get words containing the provided letters and including unknown letters
    public List<String> getWordsMatching(String providedLetters){
        List<String> matchingWords = new ArrayList<>();
        String[] lettersArray = createLettersArrayFrom(providedLetters);
        Map<String, Set<String>> wordsMap = wordsByLengthMap.get(providedLetters.length());
        if(wordsMap == null){
            return List.of();
        }
        for(String key : wordsMap.keySet()) {
            matchingWords.addAll(getWordsFromSuitableKey(wordsMap, key, lettersArray));
        }
        Collections.sort(matchingWords);
        return matchingWords;
    }


    private String[] createLettersArrayFrom(String providedLetters){
        String removedUnknowns = removeUnknownCharactersFrom(providedLetters);
        String sortedLetters= sort(removedUnknowns);
        return sortedLetters.split("");
    }


    private Set<String> getWordsFromSuitableKey(Map<String, Set<String>> wordsMap, String key, String[] lettersArray){
        boolean isKeySuitable = true;
        String tempKey = key;
        for (String s : lettersArray) {
            if (tempKey.contains(s)) {
                tempKey = tempKey.replaceFirst(s, "");
                continue;
            }
            isKeySuitable = false;
            break;
        }
        if(isKeySuitable){
            Set<String> suitableWords = wordsMap.get(key);
            if(suitableWords != null) {
                return suitableWords;
            }
        }
        return new HashSet<>();
    }


    private String removeUnknownCharactersFrom(String str){
        char UNKNOWN_CHAR = '.';
        return str.replace( String.valueOf(UNKNOWN_CHAR), "");
    }


    private List<String> getSortedListOf(Set<String> wordsSet){
        return wordsSet.stream()
                .filter(x -> x.length() > 1)
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
    }


    private void addWordsTo(Set<String> words, String sortedLetters){
        words.addAll(getAllWordsFromMap(getSearchLetters(sortedLetters)));
    }


    private void addWordsFromRepositoryTo(Set<String> words, String sortedLetters){
        words.addAll(wordsRepository.findWordsWithKey(getSearchLetters(sortedLetters)));
    }


    private Set<String> getAllWordsFromMap(String searchLetters){
        Set<String> foundWords = new HashSet<>();
        if(wordsMap.containsKey(searchLetters)) {
            Set<String> words = wordsMap.get(searchLetters);
            if (words != null) {
                foundWords.addAll(words);
            }
        }
        return foundWords;
    }


    private String sort(String str){
        char[] charArray = str.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }


    private String getSearchLetters(String letters){
        binaryCounter.inc();
        String binaryFlag = binaryCounter.getFlag();
        StringBuilder str = new StringBuilder();
        for(int i=0; i< letters.length();i++){
            if(binaryFlag.charAt(i) == '1'){
                str.append(letters.charAt(i));
            }
        }
        return str.toString();
    }

}
