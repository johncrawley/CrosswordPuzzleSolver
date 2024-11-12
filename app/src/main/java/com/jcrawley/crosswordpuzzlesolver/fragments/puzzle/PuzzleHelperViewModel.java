package com.jcrawley.crosswordpuzzlesolver.fragments.puzzle;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PuzzleHelperViewModel extends ViewModel {

    public List<String> results = new ArrayList<>();
    public boolean isUsingAnagramsForCrossword = false;
    public String resultsFoundText = "";
    public String inputText = "";

}
