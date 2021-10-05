package com.jcrawley.crosswordpuzzlesolver;

import com.jcrawley.crosswordpuzzlesolver.fragments.CrosswordHelperFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.WordExistsFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewStateAdapter extends FragmentStateAdapter {

    public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Hardcoded in this order, you'll want to use lists and make sure the titles match
        if (position == 0) {
            return new CrosswordHelperFragment();
        }
        return new WordExistsFragment();
    }

    @Override
    public int getItemCount() {
        // Hardcoded, use lists
        return 2;
    }
}