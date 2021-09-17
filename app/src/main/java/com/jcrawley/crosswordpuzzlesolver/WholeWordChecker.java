package com.jcrawley.crosswordpuzzlesolver;

import android.os.Build;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;

public class WholeWordChecker {

    private Map<String, Set<String>> wordsMap;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addWord(String word){
        String sortedKey= Arrays.stream(word.split("")).sorted().collect(Collectors.joining(""));
        System.out.println("collected! : " + sortedKey);

        if(wordsMap.containsKey(sortedKey)){
           Set<String> set = wordsMap.get(sortedKey);
           if(set != null){
               set.add(word);
           }
        }

    }


}
