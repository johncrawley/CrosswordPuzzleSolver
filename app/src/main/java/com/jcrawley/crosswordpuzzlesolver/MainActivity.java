package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private Context context;
    private EditText editText;
    private String previousSearch;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> results;
    private WordSearcher wordSearcher;
    private WholeWordChecker wholeWordChecker;
    private EditText wholeWordCheckerEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        results = new ArrayList<>();
        long beginTime = System.currentTimeMillis();
        wholeWordChecker = new WholeWordChecker();
        wholeWordCheckerEditText = findViewById(R.id.wholeWordCheckEditText);
        DictionaryLoader dictionaryLoader = new DictionaryLoader(MainActivity.this, wholeWordChecker);
        wordSearcher = new WordSearcher(dictionaryLoader.getAllWords());
        long duration = System.currentTimeMillis() - beginTime;
        System.out.println("^^^ Load time: " + duration);
        context = MainActivity.this;
        editText = findViewById(R.id.wordInput);
        setupKeyAction(editText);
        setupWholeWordCheckerKeyAction(wholeWordCheckerEditText);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(arrayAdapter);
    }


    private void search(){
        String inputText = getFormattedText(editText);
        if(inputText.isEmpty() || inputText.equals(previousSearch)){
            return;
        }
        previousSearch = inputText;
        results.clear();
        results.addAll(wordSearcher.searchFor(inputText));
        arrayAdapter.notifyDataSetChanged();
    }

    private void searchForExistingWord(){
        String inputText = getFormattedText(wholeWordCheckerEditText);
        System.out.println("^^^ word exists?  " + inputText + " : " + wholeWordChecker.doesWordExist(inputText));
    }




    private String getFormattedText(EditText editText){
        String text = editText.getText().toString();
        return text.trim().toLowerCase();
    }


    private void setupKeyAction(final EditText editText){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    search();
                    return true;
                }
                return false;
            }
        });
    }


    private void setupWholeWordCheckerKeyAction(final EditText editText){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    searchForExistingWord();
                    return true;
                }
                return false;
            }
        });
    }

}