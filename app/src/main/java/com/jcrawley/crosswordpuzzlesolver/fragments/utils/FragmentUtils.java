package com.jcrawley.crosswordpuzzlesolver.fragments.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jcrawley.crosswordpuzzlesolver.DictionaryService;
import com.jcrawley.crosswordpuzzlesolver.MainActivity;
import com.jcrawley.crosswordpuzzlesolver.WordSearcher;
import com.jcrawley.crosswordpuzzlesolver.anagram.AnagramFinder;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryHelper;
import com.jcrawley.crosswordpuzzlesolver.fragments.findWords.FindWordsFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.puzzle.PuzzleHelperFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.regex.RegexFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.regex.RegexGuideFragment;
import com.jcrawley.crosswordpuzzlesolver.fragments.wordExists.WordExistsFragment;
import com.jcrawley.crosswordpuzzlesolver.R;

import java.util.List;
import java.util.Optional;
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


    public static void loadRegexGuide(Fragment parentFragment){
        loadFragment(parentFragment, new RegexGuideFragment(), "regex_guide");
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
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                whenFinished.run();
            }
        });
    }


    public static void setupKeyboardInput(final EditText editText, final View noResultsFoundView, Context context, Runnable searchAction){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_ACTION_SEARCH) {
                return false;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm == null){
                return false;
            }
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            noResultsFoundView.setVisibility(View.GONE);
            searchAction.run();
            return true;
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


    public static Optional<DictionaryHelper> getDictionaryHelper(Fragment fragment){
        var mainActivity = (MainActivity) fragment.getActivity();
        if(mainActivity == null){
            return Optional.empty();
        }
        return Optional.of(mainActivity.getDictionaryHelper());
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


    public static void setResultsCountText(TextView textView, Context context, int numberOfResults){
        String resultsText = "";
        if(numberOfResults == 1){
            resultsText = context.getResources().getString(R.string.one_result_found_text);
        }
        else if(numberOfResults> 1){
            resultsText = context.getResources().getString(R.string.results_found_text, numberOfResults);
        }
        textView.setText(resultsText);
    }


    public static void copyWordToClipBoard(Context context, String word){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("colour", word);
        clipboard.setPrimaryClip(clip);
        toast(context, R.string.toast_copied_selected_word_to_clipboard);
    }


    public static void copyAllWordsToClipBoard(Context context, List<String> wordsList){
        String allWords = String.join(" ", wordsList);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("colour", allWords);
        clipboard.setPrimaryClip(clip);
        toast(context, R.string.toast_copied_all_words_to_clipboard);
    }


    private static void toast(Context context, int strId){
        Toast.makeText(context, strId, Toast.LENGTH_SHORT).show();
    }
}
