package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.jcrawley.crosswordpuzzlesolver.R;

public class WordOptionsDialogFragment extends DialogFragment {


    public WordOptionsDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View parentView = inflater.inflate(R.layout.word_options_dialog, container, false);
        return parentView;
    }
}
