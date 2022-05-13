package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryTestLoader;
import com.jcrawley.crosswordpuzzlesolver.io.FileHandler;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel viewModel  = new ViewModelProvider(this).get(MainViewModel.class);
        DictionaryLoader dictionaryLoader = new DictionaryLoaderImpl(this, viewModel);
        Executors.newSingleThreadExecutor().submit(dictionaryLoader::retrieveAllWords);
        setupTabLayout();
    }


    public void hideProgressIndicator(){
        new Handler(Looper.getMainLooper()).post(() -> {
         //   findViewById(R.id.loading_progress_indicator).setVisibility(View.GONE);
            findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.pager).setVisibility(View.VISIBLE);
        });
    }

    private void log(String msg){
        System.out.println("^^^ MainActivity: " + msg);
    }


    private void setupTabLayout(){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        FragmentManager fm = getSupportFragmentManager();
        ViewStateAdapter sa = new ViewStateAdapter(fm, getLifecycle());
        final ViewPager2 pa = findViewById(R.id.pager);
        pa.setAdapter(sa);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pa.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


}