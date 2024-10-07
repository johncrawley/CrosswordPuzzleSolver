package com.jcrawley.crosswordpuzzlesolver.dictionary;

import android.content.Context;

import com.jcrawley.crosswordpuzzlesolver.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class DictionaryLoaderImpl implements DictionaryLoader{

  //  private final MainViewModel viewModel;
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
    public String retrieveAllWords(){
        if(wordsStr != null){
            return wordsStr;
        }
        String words = "";
        initMaps();
        loadWordsFromFileToMaps();
        dictionaryLatch.countDown();
        return words;
    }

    @Override
    public Map<String, Set<String>> getWordsMap(){
        return wordsMap;
    }

    @Override
    public Map<Integer, Map<String, Set<String>>> getWordsByLengthMap(){
       return getWordsByLengthMap();
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
        final int MIN_LENGTH_OF_WORD = 1;
        final int MAX_LENGTH_OF_WORD = 28;
        if(wordsMap == null){
            wordsMap = new HashMap<>(50_000);
        }
        if(wordsList == null){
            wordsList = new ArrayList<>(50_000);
        }
        if(wordsByLengthMap == null){
            wordsByLengthMap = new HashMap<>(30);
            for(int i = MIN_LENGTH_OF_WORD; i< MAX_LENGTH_OF_WORD; i++){
                Map<String, Set<String>> map = new HashMap<>(500);
                wordsByLengthMap.put(i, map);
            }
        }
    }


    private void loadWordsFromFileToMaps(){
        str = new StringBuilder();
        InputStream is = context.getResources().openRawResource(R.raw.sorted_british_english);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line!= null){
                addWordSetToDataStructures(line);
                line = br.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        wordsStr = str.toString();
        log("entered loadWordsFromFileToMaps()");
        log("loadWordsFromFileToMaps() wordsStr length: " + wordsStr.length());
    }

    private void log(String msg){
        System.out.println("^^^ DictionaryLoaderImpl: " + msg);
    }


    public void loadWordsIntoDb(Consumer<String> lineConsumer){
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


    public void addWordSetToDataStructures(String wordsLine) {
        wordCount++;
        String[] lineArray = wordsLine.split(" ");
        String key = lineArray[0];
        Set<String> wordSet = new HashSet<>(Arrays.asList(lineArray).subList(1, lineArray.length));
        addWordsToStr(lineArray);
        wordsMap.put(key, wordSet);
        addWordSetToLengthMap(key, wordSet);
    }


    private void addWordSetToLengthMap(String key, Set<String> words){
        Map<String, Set<String>> lengthMap = wordsByLengthMap.get(key.length());
        if (lengthMap != null) {
            lengthMap.put(key, words);
        }
    }


    private void addWordsToStr(String[] wordsArray){
        for(int i=1; i< wordsArray.length; i++){
            str.append(" ");
            str.append(wordsArray[i]);
            wordsList.add(wordsArray[i]);
        }
    }

}
