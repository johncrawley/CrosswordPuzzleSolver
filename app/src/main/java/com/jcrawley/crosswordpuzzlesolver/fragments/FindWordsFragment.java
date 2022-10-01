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
import android.widget.TextView;

import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FindWordsFragment extends Fragment {

    private EditText editText;
    private TextView resultsCountTextView, noResultsFoundTextView;
    private Context context;
    private String previousSearch;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private AnagramFinder anagramFinder;


    public FindWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.find_words, container, false);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        anagramFinder = new AnagramFinder(viewModel, context);
        results = new ArrayList<>();
        setupViews(parentView);
        setupList(parentView);
        setupKeyAction(editText);
        return parentView;
    }


    private void setupViews(View parentView){
        editText = parentView.findViewById(R.id.lettersInputEditText);
        resultsCountTextView = parentView.findViewById(R.id.resultsCountTextView);
    }


    private void setupList(View parentView){
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView foundWordsList = parentView.findViewById(R.id.findWordsList);
        noResultsFoundTextView = parentView.findViewById(R.id.noResultsFoundText);
        foundWordsList.setAdapter(arrayAdapter);
        foundWordsList.setEmptyView(noResultsFoundTextView);
        noResultsFoundTextView.setVisibility(View.GONE);
    }


    private void setupKeyAction(final EditText editText){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm == null){
                    return false;
                }
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                noResultsFoundTextView.setVisibility(View.GONE);
                findWords();
                return true;
            }
            return false;
        });
    }


    private void findWords(){
        String inputText = getFormattedText(editText);
        if(inputText.isEmpty() || inputText.equals(previousSearch)){
            return;
        }
        previousSearch = inputText;
        results.clear();
        results.addAll(anagramFinder.getWordsFrom(inputText));
        arrayAdapter.notifyDataSetChanged();
        setResultsText();
    }


    private void setResultsText(){
        String resultsText = "";
        if(results.size() == 1){
            resultsText = context.getResources().getString(R.string.one_result_found_text);
        }
        else if(results.size() > 1){
            resultsText = context.getResources().getString(R.string.results_found_text, results.size());
        }
        resultsCountTextView.setText(resultsText);
    }


    private String getFormattedText(EditText editText){
        String text = editText.getText().toString();
        return text.trim().toLowerCase();
    }

}
