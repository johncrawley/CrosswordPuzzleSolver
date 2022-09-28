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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.material.tabs.TabLayout;
import com.jcrawley.crosswordpuzzlesolver.db.WordsRepository;
import com.jcrawley.crosswordpuzzlesolver.db.WordsRepositoryImpl;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{

    private Animation fadeOutAnimation;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFadeOutAnimation();
        MainViewModel viewModel  = new ViewModelProvider(this).get(MainViewModel.class);
        DictionaryLoader dictionaryLoader = new DictionaryLoaderImpl(this, viewModel);
        Executors.newSingleThreadExecutor().submit(dictionaryLoader::retrieveAllWords);
        //populateDbIfEmpty(dictionaryLoader);
        setupTabLayout();
    }


    public void hideProgressIndicator(){
       /*
        new Handler(Looper.getMainLooper()).post(() -> {
            findViewById(R.id.contentLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingLayout).setVisibility(View.GONE);
        });*/
        startFadeOutAnimation();
    }



    private void setupFadeOutAnimation(){

        loadingLayout = findViewById(R.id.loadingLayout);
        fadeOutAnimation = new AlphaAnimation(1, 0);
        fadeOutAnimation.setInterpolator(new AccelerateInterpolator());
        fadeOutAnimation.setStartOffset(getInt(R.integer.loading_view_fade_out_start_offset));
        fadeOutAnimation.setDuration(getInt(R.integer.loading_view_fade_out_duration));
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                loadingLayout.setVisibility(View.GONE);
                loadingLayout.clearAnimation();
                findViewById(R.id.contentLayout).setVisibility(View.VISIBLE);
            }
            public void onAnimationStart(Animation animation) { }
            public void onAnimationRepeat(Animation animation) { }
        });

    }


    private void startFadeOutAnimation(){
        new Handler(Looper.getMainLooper()).post(() -> {
            loadingLayout.startAnimation(fadeOutAnimation);
        });
    }


    private int getInt(int resId){
        return getResources().getInteger(resId);
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