package com.jcrawley.crosswordpuzzlesolver.fragments.findWords;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindWordsViewModel extends ViewModel {

    public String lettersText = "";
    public String requiredLettersText = "";
    public List<String> results = new ArrayList<>();
}
