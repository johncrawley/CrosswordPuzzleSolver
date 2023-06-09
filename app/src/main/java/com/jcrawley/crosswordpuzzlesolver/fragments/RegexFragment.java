package com.jcrawley.crosswordpuzzlesolver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegexFragment extends Fragment {

    private EditText lettersEditText;
    private TextView resultsCountTextView, noResultsFoundTextView;
    private View listDivider;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private Executor findWordsExecutor;

    private WordSearcher wordSearcher;
    public RegexFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.find_words_with_pattern, container, false);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        wordSearcher = new WordSearcher(viewModel);
        results = new ArrayList<>();
        setupViews(parentView);
        setupList(parentView);
        setupKeyAction(lettersEditText);
        findWordsExecutor = Executors.newSingleThreadExecutor();
        return parentView;
    }


    private void setupViews(View parentView){
        lettersEditText = parentView.findViewById(R.id.lettersInputEditText);
        listDivider = parentView.findViewById(R.id.listDivider);
        resultsCountTextView = parentView.findViewById(R.id.resultsCountTextView);
        setupSearchButton(parentView);
    }


    private void setupList(View parentView){
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView foundWordsList = parentView.findViewById(R.id.findWordsList);
        noResultsFoundTextView = parentView.findViewById(R.id.noResultsFoundText);
        foundWordsList.setAdapter(arrayAdapter);
        foundWordsList.setEmptyView(noResultsFoundTextView);
        noResultsFoundTextView.setVisibility(View.GONE);
    }


    private void setupSearchButton(View parentView){
        ImageButton searchButton = parentView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> findWords());
    }


    private void setupKeyAction(final EditText editText){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_ACTION_SEARCH) {
                return false;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm == null){
                return false;
            }
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            noResultsFoundTextView.setVisibility(View.GONE);
            findWords();
            return true;
        });
    }


    private void findWords(){
        findWordsExecutor.execute(()-> {
                results.clear();
                results.addAll(wordSearcher.searchForPattern(getFormattedText(lettersEditText)));
                updateViewWithResults();
        });
    }


    private void updateViewWithResults(){
        new Handler(Looper.getMainLooper()).post(()->{
            arrayAdapter.notifyDataSetChanged();
            setResultsText();
            setVisibilityOnListDivider(results.size());
        });
    }


    private void setVisibilityOnListDivider(int numberOfResults){
        listDivider.setVisibility(numberOfResults > 0 ? View.VISIBLE : View.GONE);
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

