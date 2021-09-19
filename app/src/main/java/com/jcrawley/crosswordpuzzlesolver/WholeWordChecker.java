package com.jcrawley.crosswordpuzzlesolver;

import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;

public class WholeWordChecker {

    private Map<String, Set<String>> wordsMap;

    public WholeWordChecker(){
        wordsMap = new HashMap<>(40000);
    }


    public void addWord(String word){
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
        return false;
    }


}
