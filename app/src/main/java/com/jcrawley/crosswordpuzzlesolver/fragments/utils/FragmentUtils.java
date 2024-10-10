package com.jcrawley.crosswordpuzzlesolver.fragments.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jcrawley.crosswordpuzzlesolver.DictionaryService;
import com.jcrawley.crosswordpuzzlesolver.MainActivity;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.fragments.findWords.FindWordsFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.PuzzleHelperFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.RegexFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.WordExistsFragment;
import com.jcrawley.crosswordpuzzlesolver.R;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class FragmentUtils {


    public static void showDialog(Fragment parentFragment, DialogFragment dialogFragment, String tag, Bundle bundle){
        FragmentManager fragmentManager = parentFragment.getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        removePreviousFragmentTransaction(fragmentManager, tag, fragmentTransaction);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fragmentTransaction, tag);
    }


    public static void loadFindWords(Fragment parentFragment){
        loadFragment(parentFragment, new FindWordsFragment(), "find_words");
    }


    public static void loadPuzzleHelper(Fragment parentFragment){
        loadFragment(parentFragment, new PuzzleHelperFragment(), "puzzle_helper");
    }


    public static void loadRegex(Fragment parentFragment){
        loadFragment(parentFragment, new RegexFragment(), "regex");
    }


    public static void loadCheckWord(Fragment parentFragment){
        loadFragment(parentFragment, new WordExistsFragment(), "check_word");
    }


    public static void loadFragment(Fragment parentFragment, Fragment fragment, String tag, Bundle bundle){
        FragmentManager fragmentManager = parentFragment.getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        removePreviousFragmentTransaction(fragmentManager, tag, fragmentTransaction);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.pop_enter, R.anim.pop_exit )
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }


    public static void loadFragment(Fragment parentFragment, Fragment fragment, String tag){
        loadFragment(parentFragment, fragment, tag, new Bundle());
    }


    private static void removePreviousFragmentTransaction(FragmentManager fragmentManager, String tag, FragmentTransaction fragmentTransaction){
        Fragment prev = fragmentManager.findFragmentByTag(tag);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
    }


    public static void setListener(Fragment fragment, String key, Consumer<Bundle> consumer){
        fragment.getParentFragmentManager().setFragmentResultListener(key, fragment, (requestKey, bundle) -> consumer.accept(bundle));
    }


    public static void sendMessage(Fragment fragment, String key){
        sendMessage(fragment, key, new Bundle());
    }


    public static void sendMessage(Fragment fragment, String key, Bundle bundle){
        fragment.getParentFragmentManager().setFragmentResult(key, bundle);
    }


    public static int getInt(Bundle bundle, Enum<?> tag){
        return bundle.getInt(tag.name());
    }


    public static String getStr(Bundle bundle, Enum<?> tag){
        return bundle.getString(tag.name());
    }


    public static boolean getBoolean(Bundle bundle, Enum<?> tag){
        return  bundle.getBoolean(tag.name());
    }


    public static Optional<AnagramFinder> getAnagramFinder(Fragment fragment){
        return getDictionaryObj(DictionaryService::getAnagramFinder, fragment);
    }


    public static Optional<WordSearcher> getWordSearcher(Fragment fragment) {
        return getDictionaryObj(DictionaryService::getWordSearcher, fragment);
    }

    public static void fadeOut(View view, Runnable whenFinished){
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(400);
        view.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                whenFinished.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public static void searchForResults(Fragment fragment, View resultsView, Consumer<DictionaryService> dictionaryServiceConsumer){
        getDictionaryService(fragment).ifPresent(ds -> fadeOut(resultsView, ()-> dictionaryServiceConsumer.accept(ds)));
    }


    public static Optional<DictionaryService> getDictionaryService(Fragment fragment){
        MainActivity mainActivity = (MainActivity) fragment.getActivity();
        if(mainActivity == null){
            return Optional.empty();
        }
        return mainActivity.getDictionaryService();
    }



    public static void fadeIn(View view){
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setStartOffset(400);
        anim.setDuration(400);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(anim);
    }


    public static <T> Optional<T> getDictionaryObj(Function<DictionaryService, T> getter, Fragment fragment){
        MainActivity mainActivity = (MainActivity) fragment.getActivity();
        if(mainActivity == null){
            return Optional.empty();
        }
        var dictionaryService = mainActivity.getDictionaryService();
        return dictionaryService.map(getter);
    }

}