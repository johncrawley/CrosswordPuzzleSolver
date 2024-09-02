package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils;

public class MainMenuFragment extends Fragment {

    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.main_menu, container, false);
        setupViews(parentView);
        return parentView;
    }


    private void setupViews(View parentView){
        setupButton(parentView, R.id.puzzleHelperButton, ()-> FragmentUtils.loadPuzzleHelper(this));
        setupButton(parentView, R.id.findWordsButton, ()-> FragmentUtils.loadFindWords(this));
        setupButton(parentView, R.id.wordCheckerButton, ()-> FragmentUtils.loadCheckWord(this));
        setupButton(parentView, R.id.patternWordsButton, ()-> FragmentUtils.loadRegex(this));
    }

    private void setupButton(View parentView, int buttonId, Runnable action){
        Button button = parentView.findViewById(buttonId);
        button.setOnClickListener( v-> action.run());
    }


}
