package com.jcrawley.crosswordpuzzlesolver.dictionary;

import android.content.Context;

import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.trie.DictionaryTrie;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DictionaryLoaderImpl implements DictionaryLoader{

    private final Context context;
    private final MainViewModel viewModel;
    //private final WholeWordChecker wholeWordChecker;
    private final WordMapCreator wordMapCreator;


    public DictionaryLoaderImpl(Context context, MainViewModel viewModel){
        this.context = context;
        this.viewModel = viewModel;
        if(viewModel.dictionaryTrie == null){
            viewModel.dictionaryTrie = new DictionaryTrie();
        }
        wordMapCreator = new WordMapCreator(context, viewModel);
    }


    @Override
    public String retrieveAllWords(){
        if(viewModel.wordsStr != null){
            return viewModel.wordsStr;
        }
        String words = "";
        InputStream is = context.getResources().openRawResource(R.raw.british_english);
        StringBuilder str = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line!= null){
                str.append(" ");
                str.append(line);
                wordMapCreator.addWord(line.trim());
                viewModel.dictionaryTrie.addWord(line.trim());
                line =br.readLine();
            }
            words = str.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        wordMapCreator.setupWordMap();
        viewModel.wordsStr = words;
        return words;
    }


}
