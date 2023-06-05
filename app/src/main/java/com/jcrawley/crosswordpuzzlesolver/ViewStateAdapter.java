package com.jcrawley.crosswordpuzzlesolver;

import com.jcrawley.crosswordpuzzlesolver.fragments.FindWordsUsingPatternFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.PuzzleHelperFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.FindWordsFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.WordExistsFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ViewStateAdapter extends FragmentStateAdapter {

    private List<Supplier<Fragment>> fragmentsList;

    public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragmentsList = new ArrayList<>();
        fragmentsList.add(PuzzleHelperFragment::new);
        fragmentsList.add(WordExistsFragment::new);
        fragmentsList.add(FindWordsFragment::new);
        fragmentsList.add(FindWordsUsingPatternFragment::new);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentsList.get(position).get();
        /*
        if (position == 0) {
            return new PuzzleHelperFragment();
        }
        else if(position == 1){
            return new WordExistsFragment();
        }
        else if(position == 2){
            return new FindWordsFragment();
        }
        return new FindWordsUsingPatternFragment();

         */
    }


    @Override
    public int getItemCount() {
        return fragmentsList.size();
    }
}