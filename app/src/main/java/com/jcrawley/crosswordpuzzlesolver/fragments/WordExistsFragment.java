package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcrawley.crosswordpuzzlesolver.R;

import androidx.fragment.app.Fragment;

public class WordExistsFragment  extends Fragment {
    public WordExistsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.word_exists, container, false);
    }
}