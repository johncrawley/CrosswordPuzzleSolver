package com.jcrawley.crosswordpuzzlesolver.anagram;

import android.content.Context;

import com.jcrawley.crosswordpuzzlesolver.db.WordsRepository;
import com.jcrawley.crosswordpuzzlesolver.db.WordsRepositoryImpl;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnagramFinder {

    private final BinaryCounter binaryCounter;
    private final MainViewModel viewModel;
    private final WordsRepository wordsRepository;

    public AnagramFinder(MainViewModel viewModel, Context context){
        this.viewModel = viewModel;
        this.binaryCounter = new BinaryCounter(2);
        wordsRepository = new WordsRepositoryImpl(context);
        wordsRepository.getAllWords();
    }


    public List<String> getWordsFrom(String providedLetters){
        Set<String> foundWords = new HashSet<>();
        String sortedLetters= sort(providedLetters);
        binaryCounter.init(sortedLetters.length());

        while(!binaryCounter.isIndexAtLimit()){
            addWordsTo(foundWords, sortedLetters);
            //addWordsFromRepositoryTo(foundWords, sortedLetters);
        }
        return foundWords.stream()
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
        if(viewModel.wordsMap.containsKey(searchLetters)) {
            Set<String> words = viewModel.wordsMap.get(searchLetters);
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
