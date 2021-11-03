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
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class CrosswordHelperFragment extends Fragment {

    private EditText editText;
    private Context context;
    private String previousSearch;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private WordSearcher wordSearcher;
    private TextView resultsCountTextView;


    public CrosswordHelperFragment() {
        // Required empty public constructor
    }

    private MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

        View view =  inflater.inflate(R.layout.crossword_helper, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        wordSearcher = new WordSearcher(viewModel);
        results = new ArrayList<>();
        editText = view.findViewById(R.id.wordInputEditText);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView crosswordMatchesList = view.findViewById(R.id.list1);
        crosswordMatchesList.setAdapter(arrayAdapter);
        resultsCountTextView = view.findViewById(R.id.crosswordResultsCountTextView);
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
                if(!hasASearchStarted) {
                    Executors.newSingleThreadExecutor().submit(this::searchForCrosswordMatches);
                }
                return true;
            }
            return false;
        });
    }

    private boolean hasASearchStarted;

    private void searchForCrosswordMatches(){
        hasASearchStarted = true;
        try {
            viewModel.dictionaryLatch.await();
            String inputText = getFormattedText(editText);
            if (inputText.isEmpty() || inputText.equals(previousSearch)) {
                return;
            }
            previousSearch = inputText;
            results.clear();
            results.addAll(wordSearcher.searchFor(inputText));
            setResultsText();
            arrayAdapter.notifyDataSetChanged();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally{
            hasASearchStarted = false;
        }
    }


    private String getFormattedText(EditText editText){
        String text = editText.getText().toString();
        return text.trim().toLowerCase();
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
