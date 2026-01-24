package com.jcrawley.crosswordpuzzlesolver.fragments.findWords;

import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeIn;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeOut;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.getDictionaryHelper;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.searchForResults;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.setResultsCountText;

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

import com.jcrawley.crosswordpuzzlesolver.DictionaryService;
import com.jcrawley.crosswordpuzzlesolver.MainActivity;
import com.jcrawley.crosswordpuzzlesolver.R;
import com.jcrawley.crosswordpuzzlesolver.WordListView;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FindWordsFragment extends Fragment implements WordListView {

    private EditText lettersEditText, requiredLettersEditText;
    private TextView resultsCountTextView, noResultsFoundTextView;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    private ListView resultsList;
    private FindWordsViewModel viewModel;


    public FindWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View parentView = inflater.inflate(R.layout.find_words, container, false);
        viewModel = new ViewModelProvider(this).get(FindWordsViewModel.class);
        setupViews(parentView, viewModel);
        setupList(parentView);
        setResultsText();
        setupKeyAction(lettersEditText, (text)-> viewModel.lettersText = text);
        setupKeyAction(requiredLettersEditText, (text) -> viewModel.requiredLettersText = text);
        return parentView;
    }


    @Override
    public void setWords(List<String> words) {
        new Handler(Looper.getMainLooper()).post(()-> {
            viewModel.results.clear();
            viewModel.results.addAll(words);
            arrayAdapter.notifyDataSetChanged();
            setResultsText();
            fadeIn(resultsList);
        });
    }


    private void setupViews(View parentView, FindWordsViewModel viewModel){
        lettersEditText = setupEditTextView(parentView, R.id.lettersInputEditText, viewModel.lettersText);
        requiredLettersEditText = setupEditTextView(parentView, R.id.requiredLettersInputEditText, viewModel.requiredLettersText);
        resultsCountTextView = setupTextView(parentView, R.id.resultsCountTextView, viewModel.lettersText);
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
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, viewModel.results);
        resultsList = parentView.findViewById(R.id.findWordsList);
        noResultsFoundTextView = parentView.findViewById(R.id.noResultsFoundText);
        resultsList.setAdapter(arrayAdapter);
        resultsList.setEmptyView(noResultsFoundTextView);
        noResultsFoundTextView.setVisibility(View.GONE);
    }


    private void setupKeyAction(final EditText editText, Consumer<String> viewModelConsumer){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            viewModelConsumer.accept(editText.getText().toString());
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_ACTION_SEARCH) {
                return false;
            }
            var imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm == null){
                return false;
            }
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            noResultsFoundTextView.setVisibility(View.GONE);
            searchForMatch();
            return true;
        });
    }


    private void searchForMatch(){
        var dictionaryHelper =  getDictionaryHelper(this);
        if(dictionaryHelper != null){
            fadeOut(resultsList, ()-> runSearch(dictionaryHelper));
        }
    }


    private void runSearch(DictionaryHelper dictionaryHelper){
        var input = lettersEditText.getText().toString().trim().toLowerCase();
        var requiredLetters = requiredLettersEditText.getText().toString().trim().toLowerCase();
        dictionaryHelper.findWords(input, requiredLetters, this);
    }


    private void setResultsText(){
        setResultsCountText(resultsCountTextView, getContext(), viewModel.results.size());
        if(viewModel.results.isEmpty() && !viewModel.lettersText.trim().isEmpty()) {
            noResultsFoundTextView.setVisibility(View.VISIBLE);
        }
    }

}
