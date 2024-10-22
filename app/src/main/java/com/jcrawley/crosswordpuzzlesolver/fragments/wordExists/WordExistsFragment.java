package com.jcrawley.crosswordpuzzlesolver.fragments.wordExists;

import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.fadeOut;
import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.getDictionaryService;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jcrawley.crosswordpuzzlesolver.R;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class WordExistsFragment  extends Fragment {

    private Context context;
    private WordExistsViewModel viewModel;
    private TextView statusTextView;

    public WordExistsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view =  inflater.inflate(R.layout.word_exists, container, false);
        viewModel = new ViewModelProvider(this).get(WordExistsViewModel.class);
        setupKeyAction(view.findViewById(R.id.wholeWordCheckEditText));
        statusTextView = view.findViewById(R.id.wordExistsTextView);
        setResultsText();
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
                String word = editText.getText().toString().trim();
                searchForWord(word);
                return true;
            }
            return false;
        });
    }


    private void searchForWord(String word){
        getDictionaryService(this).ifPresent(ds -> fadeOut(statusTextView, ()-> {
            showStatusMessage(word, ds.doesWordExist(word));
        } ));
    }


    private void showStatusMessage(String word, boolean doesWordExist){
        if(word.isEmpty()){
            return;
        }
        int messageId = doesWordExist ? R.string.word_exists_message : R.string.word_does_not_exist_message;
        viewModel.resultText = context.getString(messageId, word);
        setResultsText();
    }

    private void setResultsText(){
        if(!viewModel.resultText.isEmpty()){
            statusTextView.setText(viewModel.resultText);
            statusTextView.setVisibility(View.VISIBLE);
        }
    }

}