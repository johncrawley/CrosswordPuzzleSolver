package com.jcrawley.crosswordpuzzlesolver.fragments.findWords;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FindWordsFragment extends Fragment {

    private EditText lettersEditText, requiredLettersEditText;
    private TextView resultsCountTextView, noResultsFoundTextView;
    private View listDivider;
    private Context context;
    private String previousSearch;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private AnagramFinder anagramFinder;
    private Executor findWordsExecutor;


    public FindWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.find_words, container, false);
        FindWordsViewModel viewModel = new ViewModelProvider(this).get(FindWordsViewModel.class);

        anagramFinder = new AnagramFinder(viewModel.wordsMap, viewModel.wordsByLengthMap, context);
        results = new ArrayList<>();
        setupViews(parentView, viewModel);
        setupList(parentView);
        setupKeyAction(lettersEditText, (text)-> viewModel.lettersText = text);
        setupKeyAction(requiredLettersEditText, (text) -> viewModel.requiredLettersText = text);
        findWordsExecutor = Executors.newSingleThreadExecutor();
        return parentView;
    }


    private void setupViews(View parentView, FindWordsViewModel viewModel){
        lettersEditText = setupEditTextView(parentView, R.id.lettersInputEditText, viewModel.lettersText);
        requiredLettersEditText = setupEditTextView(parentView, R.id.requiredLettersInputEditText, viewModel.requiredLettersText);
        resultsCountTextView = setupTextView(parentView, R.id.resultsCountTextView, viewModel.lettersText);
        listDivider = parentView.findViewById(R.id.listDivider);
    }


    private EditText setupEditTextView(View parentView, int id, String viewModelStr){
        EditText editText = parentView.findViewById(id);
        editText.setText(viewModelStr);
        return editText;
    }


    private TextView setupTextView(View parentView, int id, String viewModelStr){
        TextView textView = parentView.findViewById(id);
        textView.setText(viewModelStr);
        return textView;
    }


    private void setupList(View parentView){
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView foundWordsList = parentView.findViewById(R.id.findWordsList);
        noResultsFoundTextView = parentView.findViewById(R.id.noResultsFoundText);
        foundWordsList.setAdapter(arrayAdapter);
        foundWordsList.setEmptyView(noResultsFoundTextView);
        noResultsFoundTextView.setVisibility(View.GONE);
    }


    private void setupKeyAction(final EditText editText, Consumer<String> viewModelConsumer){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            viewModelConsumer.accept(editText.getText().toString());
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
