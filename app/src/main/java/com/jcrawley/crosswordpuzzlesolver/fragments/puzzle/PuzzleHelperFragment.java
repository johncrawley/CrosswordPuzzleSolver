package com.jcrawley.crosswordpuzzlesolver.fragments.puzzle;

import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeIn;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.searchForResults;

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

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jcrawley.crosswordpuzzlesolver.DictionaryService;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordListView;

import java.util.List;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class PuzzleHelperFragment extends Fragment implements WordListView {

    private EditText lettersEditText, excludedLettersEditText;
    private View listDivider;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    private TextView resultsFoundTextView;
    private View noResultsFoundView;
    private PuzzleHelperViewModel viewModel;
    private ListView resultsList;
    private boolean hasSearchStarted = false;

    public PuzzleHelperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.puzzle_helper, container, false);
        viewModel = new ViewModelProvider(this).get(PuzzleHelperViewModel.class);
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
        resultsFoundTextView = parentView.findViewById(R.id.crosswordResultsCountTextView);
        assignResultsFoundText();
        listDivider = parentView.findViewById(R.id.listDivider);
        setupSwitch(parentView);
    }


    private void setupSearchButton(View parentView){
        AppCompatImageButton searchButton = parentView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> searchForMatches());
    }


    public void setupSwitch(View parentView){
        SwitchMaterial switchMaterial = parentView.findViewById(R.id.allowAnagramsSwitch);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.isUsingAnagramsForCrossword = isChecked);
    }


    private void setupList(View parentView){
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, viewModel.results);
        resultsList = parentView.findViewById(R.id.crosswordHelperList);
        noResultsFoundView = parentView.findViewById(R.id.noCrosswordResultsFoundText);
        resultsList.setEmptyView(noResultsFoundView);
        resultsList.setAdapter(arrayAdapter);
        noResultsFoundView.setVisibility(View.GONE);
    }


    private void setupKeyAction(final EditText editText){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if(hasSearchStarted){
                return false;
            }
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm == null){
                    return false;
                }
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if(!hasSearchStarted) {
                    noResultsFoundView.setVisibility(View.GONE);
                    searchForMatches();
                   // Executors.newSingleThreadExecutor().submit(this::searchForCrosswordMatches);
                }
                return true;
            }
            return false;
        });
    }


    private void searchForMatches(){
        searchForResults(this, resultsList, this::runSearch);
    }


    private void runSearch(DictionaryService dictionaryService){
        hasSearchStarted = true;
        String inputText = lettersEditText.getText().toString();
        String excludedText = excludedLettersEditText.getText().toString();
        dictionaryService.runPuzzleHelperSearch(inputText, excludedText, viewModel.isUsingAnagramsForCrossword, this);
    }


    private void updateVisibilityOnListDivider(){
        listDivider.setVisibility(!viewModel.results.isEmpty() ? View.VISIBLE : View.GONE);
    }


    private void setResultsText(){
        viewModel.resultsFoundText = "";
        int numberOfResults = viewModel.results.size();

        if(numberOfResults == 1){
            viewModel.resultsFoundText = getString(R.string.one_result_found_text);
        }
        else if(numberOfResults > 1){
            viewModel.resultsFoundText = getString(R.string.results_found_text, viewModel.results.size());
        }
        assignResultsFoundText();
    }


    private void assignResultsFoundText(){
        resultsFoundTextView.setText(viewModel.resultsFoundText);
    }


    @Override
    public void setWords(List<String> words) {
        new Handler(Looper.getMainLooper()).post(()-> {
            viewModel.results.clear();
            viewModel.results.addAll(words);
            arrayAdapter.notifyDataSetChanged();
            setResultsText();
            updateVisibilityOnListDivider();
            fadeIn(resultsList);
            hasSearchStarted = false;
        });
    }

}