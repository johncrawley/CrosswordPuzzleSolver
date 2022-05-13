package com.jcrawley.crosswordpuzzlesolver.dictionary;

import android.content.Context;

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
import java.util.Objects;
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
                String trimmed = line.trim();
                addWordToMap(trimmed);
                viewModel.dictionaryTrie.addWord(trimmed);
                line =br.readLine();
            }
            words = str.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        viewModel.wordsStr = words;
        viewModel.dictionaryLatch.countDown();
        mainActivity.hideProgressIndicator();
        return words;
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
}
