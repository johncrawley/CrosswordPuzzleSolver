package com.jcrawley.crosswordpuzzlesolver.dictionary;

import com.jcrawley.crosswordpuzzlesolver.MainActivity;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.trie.DictionaryTrie;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class DictionaryLoaderImpl implements DictionaryLoader{

    private final MainViewModel viewModel;
    private final MainActivity mainActivity;
    private StringBuilder str;


    public DictionaryLoaderImpl(MainActivity mainActivity, MainViewModel viewModel){
        this.mainActivity = mainActivity;
        this.viewModel = viewModel;
        viewModel.dictionaryLatch = new CountDownLatch(1);
        if(viewModel.dictionaryTrie == null){
            viewModel.dictionaryTrie = new DictionaryTrie();
        }
    }


    @Override
    public String retrieveAllWords(){
        if(viewModel.wordsStr != null){
            return viewModel.wordsStr;
        }
        String words = "";
        initMaps();
        loadWordsFromFileToMaps();
        viewModel.dictionaryLatch.countDown();
        return words;
    }


    private void initMaps(){
        final int MIN_LENGTH_OF_WORD = 1;
        final int MAX_LENGTH_OF_WORD = 28;
        if(viewModel.wordsMap == null){
            viewModel.wordsMap = new HashMap<>(50_000);
        }
        if(viewModel.wordsList == null){
            viewModel.wordsList = new ArrayList<>(50_000);
        }
        if(viewModel.wordsByLengthMap == null){
            viewModel.wordsByLengthMap = new HashMap<>(30);
            for(int i = MIN_LENGTH_OF_WORD; i< MAX_LENGTH_OF_WORD; i++){
                Map<String, Set<String>> map = new HashMap<>(500);
                viewModel.wordsByLengthMap.put(i, map);
            }
        }
    }


    private void loadWordsFromFileToMaps(){
        str = new StringBuilder();
        InputStream is = mainActivity.getResources().openRawResource(R.raw.sorted_british_english);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line!= null){
                addWordSetToDataStructures(line);
                line = br.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        viewModel.wordsStr = str.toString();
    }


    public void loadWordsIntoDb(Consumer<String> lineConsumer){
        str = new StringBuilder();
        InputStream is = mainActivity.getResources().openRawResource(R.raw.sorted_british_english);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line!= null){
                lineConsumer.accept(line);
                //addWordSetToMap(line);
                line =br.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        viewModel.wordsStr = str.toString();
    }


    public void addWordSetToDataStructures(String wordsLine) {
        viewModel.wordCount++;
        String[] lineArray = wordsLine.split(" ");
        String key = lineArray[0];
        Set<String> wordSet = new HashSet<>(Arrays.asList(lineArray).subList(1, lineArray.length));
        addWordsToStr(lineArray);
        viewModel.wordsMap.put(key, wordSet);
        addWordSetToLengthMap(key, wordSet);
    }


    private void addWordSetToLengthMap(String key, Set<String> words){
        Map<String, Set<String>> lengthMap = viewModel.wordsByLengthMap.get(key.length());
        if (lengthMap != null) {
            lengthMap.put(key, words);
        }
    }


    private void addWordsToStr(String[] wordsArray){
        for(int i=1; i< wordsArray.length; i++){
            str.append(" ");
            str.append(wordsArray[i]);
            viewModel.wordsList.add(wordsArray[i]);
        }
    }

}
