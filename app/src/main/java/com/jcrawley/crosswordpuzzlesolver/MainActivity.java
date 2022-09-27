package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.jcrawley.crosswordpuzzlesolver.db.WordsRepository;
import com.jcrawley.crosswordpuzzlesolver.db.WordsRepositoryImpl;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel viewModel  = new ViewModelProvider(this).get(MainViewModel.class);
        DictionaryLoader dictionaryLoader = new DictionaryLoaderImpl(this, viewModel);
        Executors.newSingleThreadExecutor().submit(dictionaryLoader::retrieveAllWords);
        //populateDbIfEmpty(dictionaryLoader);
        setupTabLayout();
        hideProgressIndicator();
    }


    public void hideProgressIndicator(){
        new Handler(Looper.getMainLooper()).post(() -> {
            findViewById(R.id.contentLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingLayout).setVisibility(View.GONE);
        });
    }


    private void populateDbOnFirstLoad(){
        final String DB_POPULATED_PREF = "is_DB_Populated";
        SharedPreferences sharedPreferences = getSharedPreferences("DictionaryPrefs", MODE_PRIVATE);
        if(!sharedPreferences.contains(DB_POPULATED_PREF)){
            SharedPreferences.Editor editor = sharedPreferences.edit().putBoolean(DB_POPULATED_PREF, true);
            editor.apply();
            Executors.newSingleThreadExecutor().submit(this::populateDb);
        }
        else{
            System.out.println("DB is apparently already populated!");
        }
    }


    private void populateDbIfEmpty(DictionaryLoader dictionaryLoader){
        WordsRepository wordsRepository = new WordsRepositoryImpl(MainActivity.this);
        if(wordsRepository.hasAnyWords()){
            return;
        }
        wordsRepository.saveWordsFromDictionary(dictionaryLoader);
    }


    private void populateDb(){
        System.out.println("Populating Database from Map file!");
        WordsRepository wordsRepository = new WordsRepositoryImpl(MainActivity.this);
        wordsRepository.saveWordsFromMapFile();
    }



    private void setupTabLayout(){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewStateAdapter viewStateAdapter = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());
        final ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(viewStateAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


}