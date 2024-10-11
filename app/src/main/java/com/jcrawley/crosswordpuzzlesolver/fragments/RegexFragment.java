package com.jcrawley.crosswordpuzzlesolver.fragments;

import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeIn;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.searchForResults;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.setupKeyboardInput;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jcrawley.crosswordpuzzlesolver.DictionaryService;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordListView;

import java.util.ArrayList;
import java.util.List;

public class RegexFragment extends Fragment implements WordListView {

    private EditText lettersEditText;
    private TextView resultsCountTextView, noResultsFoundTextView;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private ListView resultsList;

    public RegexFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.find_words_with_pattern, container, false);
        results = new ArrayList<>();
        setupViews(parentView);
        setupList(parentView);
        setupKeyboardInput(lettersEditText, noResultsFoundTextView, getContext(), this::searchForMatches);
        return parentView;
    }


    private void setupViews(View parentView){
        lettersEditText = parentView.findViewById(R.id.lettersInputEditText);
        resultsCountTextView = parentView.findViewById(R.id.resultsCountTextView);
        setupSearchButton(parentView);
    }


    private void setupList(View parentView){
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        resultsList = parentView.findViewById(R.id.findWordsList);
        noResultsFoundTextView = parentView.findViewById(R.id.noResultsFoundText);
        resultsList.setAdapter(arrayAdapter);
        resultsList.setEmptyView(noResultsFoundTextView);
        noResultsFoundTextView.setVisibility(View.GONE);
    }


    private void setupSearchButton(View parentView){
        ImageButton searchButton = parentView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> searchForMatches());
    }


    private void searchForMatches(){
        searchForResults(this, resultsList, this::runSearch);
    }


    private void runSearch(DictionaryService dictionaryService){
        String pattern = lettersEditText.getText().toString().trim();
        dictionaryService.getResultsForPattern(pattern, this);
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


    @Override
    public void setWords(List<String> words) {
        new Handler(Looper.getMainLooper()).post(()-> {
            results.clear();
            results.addAll(words);
            arrayAdapter.notifyDataSetChanged();
            setResultsText();
            fadeIn(resultsList);
        });
    }
}

