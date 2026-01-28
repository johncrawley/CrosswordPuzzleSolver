package com.jcrawley.crosswordpuzzlesolver.fragments.puzzle;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeIn;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeOut;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.getDictionaryHelper;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.setResultsCountText;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.KeyboardUtils.setupKeyAction;

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

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordListView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final AtomicBoolean hasSearchStarted = new AtomicBoolean(false);
    private ImageButton searchButton;

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
        setResultsText();
        return parentView;
    }


    private void setupViews(View parentView){
        noResultsFoundView = parentView.findViewById(R.id.noCrosswordResultsFoundText);
        lettersEditText = parentView.findViewById(R.id.wordInputEditText);
        setupKeyAction(lettersEditText, this::setVisibilityOnSearchButton, this::onKeyDone, hasSearchStarted);
        excludedLettersEditText = parentView.findViewById(R.id.excludeLettersEditText);
        setupKeyAction(excludedLettersEditText, this::onKeyDone, hasSearchStarted);
        resultsFoundTextView = parentView.findViewById(R.id.crosswordResultsCountTextView);
        listDivider = parentView.findViewById(R.id.listDivider);
        setupSwitch(parentView);
    }


    private void setupSearchButton(View parentView){
        searchButton = parentView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> searchForMatches());
    }


    public void setupSwitch(View parentView){
        SwitchMaterial switchMaterial = parentView.findViewById(R.id.allowAnagramsSwitch);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.isUsingAnagramsForCrossword = isChecked);
    }


    private void setupList(View parentView){
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, viewModel.results);
        resultsList = parentView.findViewById(R.id.crosswordHelperList);
        resultsList.setEmptyView(noResultsFoundView);
        resultsList.setAdapter(arrayAdapter);
        noResultsFoundView.setVisibility(View.GONE);
    }



    private void setVisibilityOnSearchButton(){
        var input = lettersEditText.getText().toString();
        if(input.isEmpty()){
            hideSearchButton();
        }
        else{
            showSearchButton();
        }
    }


    private void hideSearchButton(){
        searchButton.setVisibility(INVISIBLE);
    }


    private void showSearchButton(){
        searchButton.setVisibility(VISIBLE);
    }


    private void onKeyDone(){
        noResultsFoundView.setVisibility(View.GONE);
        startSearchingAnimation();
        searchForMatches();
    }


    private void searchForMatches(){
        fadeOut(resultsList, this::runSearch);
    }

    private void startSearchingAnimation(){

    }


    private void runSearch(){
        hasSearchStarted.set(true);
        viewModel.inputText = lettersEditText.getText().toString().trim();
        if(viewModel.inputText.isEmpty()){
            return;
        }
        var excludedText = excludedLettersEditText.getText().toString();
        var dictionaryHelper = getDictionaryHelper(this);
        if(dictionaryHelper != null && dictionaryHelper.isNotCurrentlySearching()){
           var results = dictionaryHelper.runPuzzleHelperSearch(viewModel.inputText, excludedText, viewModel.isUsingAnagramsForCrossword, this);
           setWords(results);
        }
    }


    private void updateVisibilityOnListDivider(){
        listDivider.setVisibility(!viewModel.results.isEmpty() ? VISIBLE : View.GONE);
    }


    private void setResultsText(){
        setResultsCountText(resultsFoundTextView, getContext(), viewModel.results.size());
        if(viewModel.results.isEmpty() && !viewModel.inputText.isEmpty()) {
            noResultsFoundView.setVisibility(VISIBLE);
            resultsList.setVisibility(View.GONE);
        }
    }

    private void log(String msg){
        System.out.println("^^^ PuzzleHelperFragment: " + msg);
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
            hasSearchStarted.set(false);
        });
    }

}
