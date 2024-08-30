package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.material.tabs.TabLayout;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.tab.TabHelper;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{

    private Animation fadeOutAnimation;
    private View loadingLayout;
    private MainViewModel viewModel;
    private TabHelper tabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        tabHelper = new TabHelper(viewModel);
        setupTabsOnViewLoaded();
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


    public void hideProgressIndicatorQuickly(){
        findViewById(R.id.loadingLayout).setVisibility(View.GONE);
        findViewById(R.id.contentLayout).setVisibility(View.VISIBLE);
    }


    public void hideProgressIndicator(){
        setupFadeOutAnimation();
        new Handler(Looper.getMainLooper()).post(() -> loadingLayout.startAnimation(fadeOutAnimation));
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


    private int getInt(int resId){
        return getResources().getInteger(resId);
    }


    private void setupTabsOnViewLoaded(){
        final ViewGroup mainLayout = findViewById(R.id.mainLayout);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (viewModel.wordsStr == null) {
                    Executors.newSingleThreadExecutor().submit(() -> setupDictionaryAndRetrieveWords());
                }
                else{
                    hideProgressIndicatorQuickly();
                }
                setupTabLayout();
            }
        });
    }


    private void setupDictionaryAndRetrieveWords() {
        new DictionaryLoaderImpl(this, viewModel).retrieveAllWords();
    }


    private void setupTabLayout(){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewStateAdapter viewStateAdapter = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());
        final ViewPager2 viewPager = findViewById(R.id.pager);
        tabHelper.setupTabLayout(tabLayout, viewPager);
        viewPager.setAdapter(viewStateAdapter);
    }

}