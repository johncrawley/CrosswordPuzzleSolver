package com.jcrawley.crosswordpuzzlesolver.fragments.findWords;

import androidx.lifecycle.ViewModel;

import java.util.Map;
import java.util.Set;

public class FindWordsViewModel extends ViewModel {

    public Map<String, Set<String>> wordsMap;
    public Map<Integer, Map<String, Set<String>>> wordsByLengthMap;
    public String lettersText = "";
    public String requiredLettersText = "";
    public String resultsCountText = "";
}
