package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryHelper;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.fragments.MainMenuFragment;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupInsets();
        setupViewModel();
        initDictionaryHelper();
        setupFragmentsIf(savedInstanceState == null);
    }


    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public Optional<DictionaryService> getDictionaryService(){
        return Optional.ofNullable(null);
    }


    public DictionaryHelper getDictionaryHelper(){
        return viewModel.dictionaryHelper;
    }


    public void initDictionaryHelper(){
        if(!viewModel.dictionaryHelper.isDictionaryLoaded()){
            var dictionaryLoader = new DictionaryLoaderImpl(MainActivity.this);
            var dictionary = dictionaryLoader.loadDictionary();
            viewModel.dictionaryHelper.init(dictionary);
        }
    }


    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }


    private void setupFragmentsIf(boolean isSavedStateNull) {
        if(!isSavedStateNull){
            return;
        }
        var mainMenuFragment = new MainMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mainMenuFragment)
                .commit();
    }

    private void log(String msg){
        System.out.println("^^^ MainActivity: " + msg);
    }

}