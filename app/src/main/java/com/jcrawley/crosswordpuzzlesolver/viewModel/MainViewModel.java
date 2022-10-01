package com.jcrawley.crosswordpuzzlesolver.viewModel;

import com.jcrawley.crosswordpuzzlesolver.trie.DictionaryTrie;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public Map<String, Set<String>> wordsMap;
    public Map<Integer, Map<String, Set<String>>> wordsByLengthMap;
    public List<String> savableWordsList;
    public List<String> wordsList;
    public int wordCount;
    public String wordsStr;
    public DictionaryTrie dictionaryTrie;
    public CountDownLatch dictionaryLatch;
    public boolean isUsingAnagramsForCrossword;

}
