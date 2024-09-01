package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.fragments.MainMenuFragment;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity{

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewModel();
        setupFragmentsIf(savedInstanceState == null);
    }


    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if(viewModel.wordsList == null || viewModel.wordsList.isEmpty()){
            Executors.newSingleThreadExecutor().submit(
                    ()->  new DictionaryLoaderImpl(this, viewModel).retrieveAllWords());
        }
    }


    private void setupFragmentsIf(boolean isSavedStateNull) {
        if(!isSavedStateNull){
            return;
        }
        Fragment mainMenuFragment = new MainMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mainMenuFragment)
                .commit();
    }


}