package com.jcrawley.crosswordpuzzlesolver.dictionary;

import com.jcrawley.crosswordpuzzlesolver.WholeWordChecker;

import java.util.Arrays;
import java.util.List;

public class DictionaryTestLoader  implements DictionaryLoader{

    private final List<String> words = Arrays.asList("the", "quick", "brown", "fox");

    public DictionaryTestLoader(WholeWordChecker wholeWordChecker){
        for(String word : words){
            wholeWordChecker.addWord(word);
        }
    }


    public String getAllWords(){
        StringBuilder str = new StringBuilder();
        for(String word: words){
            str.append(word);
            str.append(" ");
        }
        return str.toString();
    }
}
