package com.jcrawley.crosswordpuzzlesolver.fragments.regex;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class RegexViewModel extends ViewModel {

    public List<String> results = new ArrayList<>();
    public String resultsFoundText = "";
    public String inputText = "";

}
