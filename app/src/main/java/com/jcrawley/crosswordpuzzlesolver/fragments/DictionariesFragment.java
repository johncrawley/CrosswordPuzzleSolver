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
import android.widget.ListView;
import android.widget.TextView;

import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DictionariesFragment extends Fragment {

    private EditText lettersEditText, requiredLettersEditText;
    private TextView resultsCountTextView, noResultsFoundTextView;
    private View listDivider;
    private Context context;
    private String previousSearch;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private AnagramFinder anagramFinder;
    private Executor findWordsExecutor;


    public DictionariesFragment() {
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
        setupKeyAction(lettersEditText);
        setupKeyAction(requiredLettersEditText);
        findWordsExecutor = Executors.newSingleThreadExecutor();
        return parentView;
    }


    private void setupViews(View parentView){
        lettersEditText = parentView.findViewById(R.id.lettersInputEditText);
        requiredLettersEditText = parentView.findViewById(R.id.requiredLettersInputEditText);
        listDivider = parentView.findViewById(R.id.listDivider);
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
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_ACTION_SEARCH) {
                return false;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm == null){
                return false;
            }
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            noResultsFoundTextView.setVisibility(View.GONE);
            findWordsExecutor.execute(this::findWords);
            return true;
        });
    }


    private void findWords(){
        String inputText = getFormattedText(lettersEditText) + getFormattedText(requiredLettersEditText);
        if(inputText.isEmpty() || inputText.equals(previousSearch)){
            return;
        }
        previousSearch = inputText;
        results.clear();
        results.addAll(filterResultsWithRequiredLetters(anagramFinder.getWordsFrom(inputText)));
        updateViewWithResults();

    }

    private List<String> filterResultsWithRequiredLetters(List<String> words){
        List<String> requiredLetters = createRequiredLettersList();
        if(requiredLetters.isEmpty()){
            return words;
        }
        return words.stream().filter(word -> doesWordHaveAllLetters(word, requiredLetters)).collect(Collectors.toList());
    }


    private List<String> createRequiredLettersList(){
        String requiredLettersStr = requiredLettersEditText.getText().toString().trim();
        String azRequiredLetters = requiredLettersStr.replaceAll("[^A-za-z]", "");
        return azRequiredLetters.isEmpty() ? Collections.emptyList() : Arrays.asList(azRequiredLetters.split(""));
    }


    private boolean doesWordHaveAllLetters(String word, List<String> letters){
        return letters.stream().allMatch(word::contains);
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
