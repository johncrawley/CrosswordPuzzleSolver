package com.jcrawley.crosswordpuzzlesolver.viewModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.lifecycle.ViewModel;

import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryHelper;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryHelperImpl;

public class MainViewModel extends ViewModel {

    public Map<String, Set<String>> wordsMap;
    public List<String> wordsList;
    public int wordCount;
    public DictionaryHelper dictionaryHelper = new DictionaryHelperImpl();

}
