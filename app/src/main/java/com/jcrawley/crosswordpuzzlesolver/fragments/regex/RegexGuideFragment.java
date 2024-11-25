package com.jcrawley.crosswordpuzzlesolver.fragments.regex;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.jcrawley.crosswordpuzzlesolver.R;

public class RegexGuideFragment extends Fragment {

    public RegexGuideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.regex_guide, container, false);

        return parentView;
    }
}