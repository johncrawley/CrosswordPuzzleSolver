package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryTestLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private Context context;
    private EditText editText;
    private WholeWordChecker wholeWordChecker;
    private EditText wholeWordCheckerEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTabLayout();
        long beginTime = System.currentTimeMillis();
        wholeWordChecker = new WholeWordChecker();
        wholeWordCheckerEditText = findViewById(R.id.wholeWordCheckEditText);
        long duration = System.currentTimeMillis() - beginTime;
        System.out.println("^^^ Load time: " + duration);
        context = MainActivity.this;
        editText = findViewById(R.id.wordInputEditText);
       // setupKeyAction(editText);
       // setupWholeWordCheckerKeyAction(wholeWordCheckerEditText);
       // arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, results);
       // ListView crosswordMatchesList = findViewById(R.id.list);
       // crosswordMatchesList.setAdapter(arrayAdapter);
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

/*

    private void searchForExistingWord(){
        //String inputText = getFormattedText(wholeWordCheckerEditText);
        boolean doesWordExist = wholeWordChecker.doesWordExist(inputText);
        System.out.println("^^^ word exists?  " + inputText + " : " + doesWordExist );
        ImageView statusImageView = findViewById(R.id.wordExistsStatusImageView);

        if(doesWordExist){
            statusImageView.setVisibility(View.VISIBLE);
            setImageDrawable(statusImageView, R.drawable.correct_icon);

        }
        else{
            statusImageView.setVisibility(View.VISIBLE);
            setImageDrawable(statusImageView, R.drawable.incorrect_icon);
        }
    }

 */

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImageDrawable(ImageView imageView, int drawableId){
        imageView.setImageDrawable(getResources().getDrawable(drawableId, null));
    }


/*

    private void setupWholeWordCheckerKeyAction(final EditText editText){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    searchForExistingWord();
                    return true;
                }
                return false;
            }
        });
    }
*/
}