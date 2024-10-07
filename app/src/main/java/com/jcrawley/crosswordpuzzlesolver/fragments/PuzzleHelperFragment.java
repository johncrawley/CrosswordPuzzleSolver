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
import com.jcrawley.crosswordpuzzlesolver.DictionaryService;
import com.jcrawley.crosswordpuzzlesolver.MainActivity;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordListView;
import com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class PuzzleHelperFragment extends Fragment implements WordListView {

    private EditText lettersEditText, excludedLettersEditText;
    private View listDivider;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private TextView resultsCountTextView;
    private boolean hasSearchStarted;
    private View noResultsFoundView;
    private MainViewModel viewModel;
    private ListView resultsList;

    public PuzzleHelperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.puzzle_helper, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
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
        AppCompatImageButton searchButton = parentView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> searchForMatches());
    }


    public void setupSwitch(View parentView){
        SwitchMaterial switchMaterial = parentView.findViewById(R.id.allowAnagramsSwitch);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.isUsingAnagramsForCrossword = isChecked);
    }


    private void setupList(View parentView){
        results = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        resultsList = parentView.findViewById(R.id.crosswordHelperList);
        noResultsFoundView = parentView.findViewById(R.id.noCrosswordResultsFoundText);
        resultsList.setEmptyView(noResultsFoundView);
        resultsList.setAdapter(arrayAdapter);
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
                    searchForMatches();
                   // Executors.newSingleThreadExecutor().submit(this::searchForCrosswordMatches);
                }
                return true;
            }
            return false;
        });
    }


    private void searchForMatches(){
        getDictionaryService().ifPresent(ds -> {
            log("dictionaryService was present: running search");
            String inputText = lettersEditText.getText().toString();
            String excludedText = excludedLettersEditText.getText().toString();
            ds.runPuzzleHelperSearch(inputText, excludedText, viewModel.isUsingAnagramsForCrossword, this);
        });
    }


    private Optional<DictionaryService> getDictionaryService(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity == null){
            return Optional.empty();
        }
        return mainActivity.getDictionaryService();
    }


    private void updateVisibilityOnListDivider(){
        listDivider.setVisibility(!results.isEmpty() ? View.VISIBLE : View.GONE);
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


    @Override
    public void setWords(List<String> words) {
        new Handler(Looper.getMainLooper()).post(()-> {
            arrayAdapter.notifyDataSetChanged();
            setResultsText();
            updateVisibilityOnListDivider();
        });
    }


    @Override
    public void fadeOutList() {
        FragmentUtils.fadeOut(resultsList);
    }


    @Override
    public void fadeInList() {
        FragmentUtils.fadeIn(resultsList);

    }
}
