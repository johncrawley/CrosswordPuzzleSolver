package com.jcrawley.crosswordpuzzlesolver.anagram;


import com.jcrawley.crosswordpuzzlesolver.db.WordsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    private Map<Character, Integer> requiredLettersMap;

    public AnagramFinder(){

    }


    public void setupWordsMap( Map<String, Set<String>> wordsMap){
        this.wordsMap = wordsMap;
    }


    public void setWordsByLengthMap(Map<Integer, Map<String, Set<String>>> wordsByLengthMap){
        this.wordsByLengthMap = wordsByLengthMap;
    }


   /*
       The binary counter is used to iterate over all the combinations of input letters
       For a 5 letter input, for example, it counts from 00001 to 11111
       For a given counter value, the input letters chosen for checking  will correspond to the '1's
       E.g. with a binary value of 01101 and the (sorted) input letters 'abemt', 'bet' will be checked
        The next iteration would be 01110, and thus 'bem' will be checked
     */

    public List<String> getWordsFrom(String providedLetters){
        String justLetters = providedLetters.trim();
        if(justLetters.isEmpty()){
            return Collections.emptyList();
        }
        Set<String> foundWords = new HashSet<>();
        String sortedLetters = sort(justLetters);
        binaryCounter = new BinaryCounter(sortedLetters.length());

        while(!binaryCounter.isIndexAtLimit()){
            addWordsTo(foundWords, sortedLetters);
            //addWordsFromRepositoryTo(foundWords, sortedLetters);
        }
        return getSortedListOf(foundWords);
    }


    private void addWordsTo(Set<String> words, String sortedLetters){
        words.addAll(getAllWordsFromMap(getSearchLetters(sortedLetters)));
    }


    private String getSearchLetters(String letters){
        binaryCounter.inc();
        String binaryFlag = binaryCounter.getFlag();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < letters.length(); i++){
            if(binaryFlag.charAt(i) == '1'){
                str.append(letters.charAt(i));
            }
        }
        return str.toString();
    }


    public List<String> getWordsFrom(String providedLetters, String requiredLetters){
        String completeInput = providedLetters + requiredLetters;
        initRequiredLettersMap(requiredLetters);
        List<String> results = getWordsFrom(completeInput);
        return requiredLetters.trim().isEmpty()? results : filterResultsWithMultipleLetters(results);
    }


    private void initRequiredLettersMap(String requiredLetters){
        requiredLettersMap = buildLettersMapFrom(requiredLetters);
    }


    private List<String> filterResultsWithMultipleLetters(List<String> results){
        return results.stream().filter(this::hasAllRequiredChars).collect(Collectors.toList());
    }


    private boolean hasAllRequiredChars(String result){
        if(requiredLettersMap.isEmpty()){
            return true;
        }
        Map<Character, Integer> resultLettersMap = buildLettersMapFrom(result);

        for(char charVal : requiredLettersMap.keySet()){
            Integer requiredCount = requiredLettersMap.get(charVal);
            Integer resultCount = resultLettersMap.get(charVal);
            if (requiredCount == null || resultCount == null) {
                return false;
            }
            if (requiredCount > resultCount) {
                return false;
            }
        }
        return true;
    }


    private Map<Character, Integer> buildLettersMapFrom(String str){
        Map <Character, Integer> map = new HashMap<>();
        for(int i = 0; i < str.length(); i++){
            map.merge(str.charAt(i), 1, Integer::sum);
        }
        return map;
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

}
