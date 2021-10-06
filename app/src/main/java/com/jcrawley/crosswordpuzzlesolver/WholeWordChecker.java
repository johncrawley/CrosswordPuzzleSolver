package com.jcrawley.crosswordpuzzlesolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;



public class WholeWordChecker {

    private final Map<String, Set<String>> wordsMap;
    private int wordCount;

    public WholeWordChecker(){
        wordsMap = new HashMap<>(120_000);
    }

    public void createWordMapFile(){




    }


    public void addWord(String word){
        wordCount++;
        String sortedKey= getSortedWord(word);
        if(!wordsMap.containsKey(sortedKey)){
            Set<String> set = new HashSet<>();
            set.add(word);
            wordsMap.put(sortedKey, set);
        }
       Objects.requireNonNull(wordsMap.get(sortedKey)).add(word);
    }

    public String getSortedWord(String word){
        return Arrays.stream(word.split("")).sorted().collect(Collectors.joining(""));
    }

    public void printKeys(){
        for(String key : wordsMap.keySet()){
            System.out.println("^^^key: " + key);
        }
    }

    public boolean doesWordExist(String word){
        Set<String> set = wordsMap.get(getSortedWord(word));
        if(set!= null){
            return set.contains(word);
        }
        System.out.println("^^^ WholeWordChecker.doesWordExist() word count: " + wordCount);
        return false;
    }


}
