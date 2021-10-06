package com.jcrawley.crosswordpuzzlesolver.dictionary;

import android.content.Context;

import com.jcrawley.crosswordpuzzlesolver.io.FileHandler;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class WordMapCreator {

    private final Context context;
    private final MainViewModel viewModel;
    private final String DELIMITER = ",,";


    public WordMapCreator(Context context, MainViewModel viewModel){
        this.context = context;
        this.viewModel = viewModel;
        if(viewModel.wordsList == null){
            viewModel.wordsList = new ArrayList<>(120_000);
        }
    }


    public void addWord(String word){
        viewModel.wordsList.add(word);
    }


    public void addWordToMap(String word){
        viewModel.wordCount++;
        String sortedKey= getSortedWord(word);
        if(!viewModel.wordsMap.containsKey(sortedKey)){
            Set<String> set = new HashSet<>();
            set.add(word);
            viewModel.wordsMap.put(sortedKey, set);
        }
        Objects.requireNonNull(viewModel.wordsMap.get(sortedKey)).add(word);
    }


    public String getSortedWord(String word){
        return Arrays.stream(word.split("")).sorted().collect(Collectors.joining(""));
    }


    private List<String> createSavableWordsList(){
        List<String> savableWordsList = new ArrayList<>(80_000);
        for(String key: viewModel.wordsMap.keySet()){
            Set<String> wordSet = viewModel.wordsMap.get(key);

            StringBuilder stringBuilder = new StringBuilder(key);
            if(wordSet == null){
                continue;
            }
            for(String str : wordSet){
                stringBuilder.append(DELIMITER);
                stringBuilder.append(str);
            }
            savableWordsList.add(stringBuilder.toString());
        }
        return savableWordsList;
    }


    private void parseSavedWordsList(List<String> savableWordsList){
        for(String wordKeyEntry : savableWordsList){
            String[] wordsArray = wordKeyEntry.split(DELIMITER);
            String key = wordsArray[0];
            Set<String> wordsSet = new HashSet<>();
            final boolean result = wordsSet.addAll(Arrays.asList(wordsArray).subList(1, wordsArray.length));
            if(result) {
                viewModel.wordsMap.put(key, wordsSet);
            }
        }
    }


    public void setupWordMap(){
        if(viewModel.wordsMap != null) {
            return;
        }
        viewModel.wordsMap = new HashMap<>();
        FileHandler fileHandler = new FileHandler(context);
        if(!fileHandler.doesFileExist()){
            createWordsMap();
            fileHandler.writeWordsToFile(createSavableWordsList());
            return;
        }
        parseSavedWordsList(fileHandler.loadWordEntriesFromFile());
    }


    private void createWordsMap(){
        for(String word : viewModel.wordsList){
            addWordToMap(word);
        }
    }
}
