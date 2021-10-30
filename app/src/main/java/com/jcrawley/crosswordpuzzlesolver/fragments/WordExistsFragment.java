package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.Arrays;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class WordExistsFragment  extends Fragment {

    private Context context;
    private MainViewModel viewModel;
    private TextView statusMessage;

    public WordExistsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view =  inflater.inflate(R.layout.word_exists, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setupKeyAction(view.findViewById(R.id.wholeWordCheckEditText));
        statusMessage = view.findViewById(R.id.wordExistsTextView);
        return view;
    }


    private void setupKeyAction(final EditText editText){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm == null){
                    return false;
                }
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                String word = editText.getText().toString().trim();
                boolean doesWordExist = viewModel.dictionaryTrie.doesWordExist(word);
                showStatusMessage(word, doesWordExist);
                return true;
            }
            return false;
        });
    }


    private void showStatusMessage(String word, boolean doesWordExist){
        int messageId = doesWordExist ? R.string.word_exists_message : R.string.word_does_not_exist_message;
        statusMessage.setText(context.getString(messageId, word));
        statusMessage.setVisibility(View.VISIBLE);
    }

}