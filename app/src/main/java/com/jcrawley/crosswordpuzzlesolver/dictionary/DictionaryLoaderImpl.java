package com.jcrawley.crosswordpuzzlesolver.dictionary;

import android.content.Context;

import com.jcrawley.crosswordpuzzlesolver.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class DictionaryLoaderImpl implements DictionaryLoader{

    private final Context context;
    private StringBuilder str;
    private String wordsStr;
    public Map<String, Set<String>> wordsMap;
    public Map<Integer, Map<String, Set<String>>> wordsByLengthMap;
    public List<String> wordsList;
    public int wordCount;
    public CountDownLatch dictionaryLatch;
    

    public DictionaryLoaderImpl(Context context){
        this.context = context;
        dictionaryLatch = new CountDownLatch(1);
    }


    @Override
    public String getWordsStr(){
        return wordsStr;
    }


    @Override
    public void retrieveAllWords(){
        if(wordsStr != null){
            return;
        }
        initMaps();
        long startTime = System.currentTimeMillis();
        loadWordsFromFileToMaps();
        long duration = System.currentTimeMillis() - startTime;
        log("retrieveAllWords() time taken: " + duration);
        dictionaryLatch.countDown();
    }

    @Override
    public CountDownLatch getDictionaryLatch(){
        return dictionaryLatch;
    }


    @Override
    public Map<String, Set<String>> getWordsMap(){
        return wordsMap;
    }


    @Override
    public Map<Integer, Map<String, Set<String>>> getWordsByLengthMap(){
       return wordsByLengthMap;
    }


    @Override
    public List<String> getWordsList(){
        return wordsList;
    }


    @Override
    public int getWordCount(){
        return wordCount;
    }


    private void initMaps(){
        if(wordsMap == null){
            wordsMap = new HashMap<>(110_000);
        }
        if(wordsList == null){
            wordsList = new ArrayList<>(110_000);
        }
        initWordsByLengthMap();
    }


    private void initWordsByLengthMap(){
        final int MIN_LENGTH_OF_WORD = 1;
        final int MAX_LENGTH_OF_WORD = 28;
        if(wordsByLengthMap == null){
            wordsByLengthMap = new HashMap<>(30);
            for(int i = MIN_LENGTH_OF_WORD; i < MAX_LENGTH_OF_WORD; i++){
                Map<String, Set<String>> map = new HashMap<>(5_000);
                wordsByLengthMap.put(i, map);
            }
        }
    }


    private void loadWordsFromFileToMaps(){
        log("entered loadWordsFromFileToMaps()");
        str = new StringBuilder();
        var is = context.getResources().openRawResource(R.raw.sorted_british_english_2);
        try(var br = new BufferedReader(new InputStreamReader(is))) {
            String key = br.readLine();
            String word = br.readLine();
            while (key != null && word != null){
                add(word);
                addWordSetToDataStructures(key, word);
                key = br.readLine();
                word = br.readLine();
            }
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        wordsStr = str.toString();
    }


    private void log(String msg){
        System.out.println("^^^ DictionaryLoaderImpl: " + msg);
    }


    public void addWordSetToDataStructures(String key, String word) {
        wordCount++;
        var wordSet = wordsMap.computeIfAbsent(key, k -> new HashSet<>() );
        wordSet.add(word);
      //  addWordSetToLengthMap(key, wordSet);
    }


    private void add(String word){
        str.append(" ");
        str.append(word);
        wordsList.add(word);
    }


    private void addWordSetToLengthMap(String key, Set<String> words){
        var lengthMap = wordsByLengthMap.get(key.length());
        if(lengthMap != null){
            lengthMap.put(key, words);
        }
    }


    @Override
    public void loadWordsIntoDb(Consumer<String> lineConsumer){
        log("Entered loadWordsIntoDB()");
        str = new StringBuilder();
        InputStream is = context.getResources().openRawResource(R.raw.sorted_british_english);
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
        wordsStr = str.toString();
    }

}
