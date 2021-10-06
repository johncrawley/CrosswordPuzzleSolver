package com.jcrawley.crosswordpuzzlesolver.viewModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public String test = "hello there!";
    public Map<String, Set<String>> wordsMap;
    public List<String> savableWordsList;
    public List<String> wordsList;
    public int wordCount;
    public String wordsStr;

}
