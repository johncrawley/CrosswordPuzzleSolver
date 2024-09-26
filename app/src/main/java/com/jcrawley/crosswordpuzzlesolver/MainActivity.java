package com.jcrawley.crosswordpuzzlesolver;

import static com.jcrawley.crosswordpuzzlesolver.fragments.utils.FragmentUtils.sendMessage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;
import com.jcrawley.crosswordpuzzlesolver.fragments.MainMenuFragment;
import com.jcrawley.crosswordpuzzlesolver.viewModel.MainViewModel;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private final AtomicBoolean isServiceConnected = new AtomicBoolean(false);
    private DictionaryService dictionaryService;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            log("Entered onServiceConnected()");
            DictionaryService.LocalBinder binder = (DictionaryService.LocalBinder) service;
            dictionaryService = binder.getService();
            dictionaryService.setActivity(MainActivity.this);
            isServiceConnected.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            log("Entered onServiceDisconnected()");
            isServiceConnected.set(false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewModel();
        setupFragmentsIf(savedInstanceState == null);
    }


    private void setupViewModel(){
        viewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        if(viewModel.wordsList == null || viewModel.wordsList.isEmpty()){
            Executors.newSingleThreadExecutor().submit(
                    ()->  new DictionaryLoaderImpl(this).retrieveAllWords());
        }
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

    private void log(String msg){
        System.out.println("MainActivity: " + msg);
    }

}