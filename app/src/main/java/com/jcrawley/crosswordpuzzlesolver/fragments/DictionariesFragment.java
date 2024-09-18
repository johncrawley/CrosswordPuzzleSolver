package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcrawley.crosswordpuzzlesolver.R;

import androidx.fragment.app.Fragment;

public class DictionariesFragment extends Fragment {


    public DictionariesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.find_words, container, false);
        return parentView;
    }



}
