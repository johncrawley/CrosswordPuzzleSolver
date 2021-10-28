package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel viewModel  = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.test = viewModel.test + " from MainActivity!";
        DictionaryLoader dictionaryLoader = new DictionaryLoaderImpl(this, viewModel);
        dictionaryLoader.retrieveAllWords();
        findViewById(R.id.loading_progress_indicator).setVisibility(View.GONE);
        setupTabLayout();
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