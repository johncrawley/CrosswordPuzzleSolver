package com.jcrawley.crosswordpuzzlesolver.dictionary;

import com.jcrawley.crosswordpuzzlesolver.MainActivity;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.trie.DictionaryTrie;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class DictionaryLoaderImpl implements DictionaryLoader{

    private final MainViewModel viewModel;
    private final MainActivity mainActivity;


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
        long startTime = System.currentTimeMillis();
        if(viewModel.wordsStr != null){
            mainActivity.hideProgressIndicator();
            return viewModel.wordsStr;
        }
        String words = "";
        if(viewModel.wordsMap == null){
            viewModel.wordsMap = new HashMap<>(50_000);
        }
        InputStream is = mainActivity.getResources().openRawResource(R.raw.british_english);
        StringBuilder str = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line!= null){
                str.append(" ");
                str.append(line);
                viewModel.dictionaryTrie.addWord(line);
                line =br.readLine();
            }
            viewModel.wordsStr = str.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        createWordMap();
        long timeElapsed =  System.currentTimeMillis() - startTime;
        System.out.println("time taken to create words String: " + timeElapsed );
        viewModel.dictionaryLatch.countDown();
        mainActivity.hideProgressIndicator();
        return words;
    }


    private void createWordMap(){
       // long startTime = System.currentTimeMillis();
        InputStream is = mainActivity.getResources().openRawResource(R.raw.sorted_british_english);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line!= null){
                addWordSetToMap(line);
                line =br.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
       // long timeElapsed =  System.currentTimeMillis() - startTime;
       // System.out.println("time taken to create words String: " + timeElapsed );
    }


    public void addWordSetToMap(String wordsLine){
        viewModel.wordCount++;
        String[] words = wordsLine.split(" ");
        String key = words[0];

        if(!viewModel.wordsMap.containsKey(key)){
            Set<String> wordSet = new HashSet<>(Arrays.asList(words).subList(1, words.length));
            viewModel.wordsMap.put(key, wordSet);
        }
    }


    public String getSortedWord(String word){
        return Arrays.stream(word.split("")).sorted().collect(Collectors.joining(""));
    }
}
