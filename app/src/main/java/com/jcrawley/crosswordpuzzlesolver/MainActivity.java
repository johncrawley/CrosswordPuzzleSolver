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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        results = new ArrayList<>();
        DictionaryLoader dictionaryLoader = new DictionaryLoader(MainActivity.this);
        wordSearcher = new WordSearcher(dictionaryLoader.getAllWords());
        context = MainActivity.this;
        editText = findViewById(R.id.wordInput);
        setupKeyAction(editText);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(arrayAdapter);
    }


    private void search(){

        String inputText = editText.getText().toString();
        inputText = inputText.trim();
        inputText = inputText.toLowerCase();
        if(inputText.isEmpty() || inputText.equals(previousSearch)){
            return;
        }
        previousSearch = inputText;
        results.clear();
        results.addAll(wordSearcher.searchFor(inputText));
        arrayAdapter.notifyDataSetChanged();
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

}