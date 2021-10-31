package com.jcrawley.crosswordpuzzlesolver.viewModel;

import com.jcrawley.crosswordpuzzlesolver.trie.DictionaryTrie;

import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public Map<String, Set<String>> wordsMap;
    public List<String> savableWordsList;
    public List<String> wordsList;
    public int wordCount;
    public String wordsStr;
    public DictionaryTrie dictionaryTrie;

}
