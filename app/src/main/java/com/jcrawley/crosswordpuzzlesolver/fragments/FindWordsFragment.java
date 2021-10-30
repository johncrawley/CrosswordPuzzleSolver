package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FindWordsFragment extends Fragment {

    private EditText editText;
    private Context context;
    private String previousSearch;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private WordSearcher wordSearcher;


    public FindWordsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

        View view =  inflater.inflate(R.layout.find_words, container, false);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        wordSearcher = new WordSearcher(viewModel.wordsStr);
        results = new ArrayList<>();
        editText = view.findViewById(R.id.lettersInputEditText);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView foundWordsList = view.findViewById(R.id.findWordsList);
        foundWordsList.setAdapter(arrayAdapter);

        setupKeyAction(editText);

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
                searchForWords();
                return true;
            }
            return false;
        });
    }


    private void searchForWords(){
        String inputText = getFormattedText(editText);
        if(inputText.isEmpty() || inputText.equals(previousSearch)){
            return;
        }
        previousSearch = inputText;
        results.clear();
        results.addAll(wordSearcher.searchFor(inputText));
        arrayAdapter.notifyDataSetChanged();
    }


    private String getFormattedText(EditText editText){
        String text = editText.getText().toString();
        return text.trim().toLowerCase();
    }

}
