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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class PuzzleHelperFragment extends Fragment {

    private EditText lettersEditText, excludedLettersEditText;
    private View listDivider;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private WordSearcher wordSearcher;
    private TextView resultsCountTextView;
    private boolean hasSearchStarted;
    private View noResultsFoundView;
    private MainViewModel viewModel;
    private AnagramFinder anagramFinder;

    public PuzzleHelperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.puzzle_helper, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        wordSearcher = new WordSearcher(viewModel);
        anagramFinder = new AnagramFinder(viewModel, context);
        setupViews(parentView);
        setupList(parentView);
        setupSearchButton(parentView);
        return parentView;
    }


    private void setupViews(View parentView){
        lettersEditText = parentView.findViewById(R.id.wordInputEditText);
        setupKeyAction(lettersEditText);
        excludedLettersEditText = parentView.findViewById(R.id.excludeLettersEditText);
        setupKeyAction(excludedLettersEditText);
        resultsCountTextView = parentView.findViewById(R.id.crosswordResultsCountTextView);
        listDivider = parentView.findViewById(R.id.listDivider);
        setupSwitch(parentView);
    }


    private void setupSearchButton(View parentView){
        Button searchButton = parentView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> runSearch());
    }


    public void setupSwitch(View parentView){
        SwitchMaterial switchMaterial = parentView.findViewById(R.id.allowAnagramsSwitch);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.isUsingAnagramsForCrossword = isChecked);
    }


    private void setupList(View parentView){
        results = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView crosswordMatchesList = parentView.findViewById(R.id.crosswordHelperList);
        noResultsFoundView = parentView.findViewById(R.id.noCrosswordResultsFoundText);
        crosswordMatchesList.setEmptyView(noResultsFoundView);
        crosswordMatchesList.setAdapter(arrayAdapter);
        noResultsFoundView.setVisibility(View.GONE);
    }


    private void setupKeyAction(final EditText editText){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm == null){
                    return false;
                }
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if(!hasSearchStarted) {
                    noResultsFoundView.setVisibility(View.GONE);
                    Executors.newSingleThreadExecutor().submit(this::searchForCrosswordMatches);
                }
                return true;
            }
            return false;
        });
    }


    private void searchForCrosswordMatches(){
        hasSearchStarted = true;
        try {
            viewModel.dictionaryLatch.await();
            runSearch();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally{
            hasSearchStarted = false;
        }
    }


    private void runSearch(){
        String inputText = getFormattedText(lettersEditText);
        if (inputText.isEmpty()) {
            return;
        }
        results.clear();
        var initialResults = getInitialResultsFor(inputText);
        results.addAll(excludeWordsWithBanishedLetters(initialResults));
        updateViewWithResults();
    }


    private List<String> getInitialResultsFor(String inputText){
        var words = viewModel.isUsingAnagramsForCrossword ?
                anagramFinder.getWordsMatching(inputText) : wordSearcher.searchFor(inputText);
        return new ArrayList<>(words);
    }


    private void updateViewWithResults(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()-> {
            arrayAdapter.notifyDataSetChanged();
            setResultsText();
            updateVisibilityOnListDivider();
        });
    }


    private void updateVisibilityOnListDivider(){
        listDivider.setVisibility(results.size() > 0 ? View.VISIBLE : View.GONE);
    }


    private List<String> excludeWordsWithBanishedLetters(List<String> initialResults){
        String excludedLettersStr = excludedLettersEditText.getText().toString();
        if(excludedLettersStr.isEmpty()){
            return new ArrayList<>(initialResults);
        }
        List<String> excludedLetters = Arrays.asList(excludedLettersStr.split(""));
        return createListOfAllowedWords(initialResults, excludedLetters);
    }


    private List<String> createListOfAllowedWords(List<String> inputList, List<String> excludedLetters){
        return inputList.stream().filter(word -> isWordFreeOfExcludedLetters(word, excludedLetters)).collect(Collectors.toList());
    }


    private boolean isWordFreeOfExcludedLetters(String word, List<String> excludedLetters){
        String lowercaseWord = word.toLowerCase();
        return excludedLetters.stream().noneMatch(lowercaseWord::contains);
    }


    private String getFormattedText(EditText editText){
        String text = editText.getText().toString();
        return text.trim().toLowerCase();
    }


    private void log(String msg){
        System.out.println("^^^ CrosswordHelperFragment: " + msg);
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

}
