package com.jcrawley.crosswordpuzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.jcrawley.crosswordpuzzlesolver.fragments.MainMenuFragment;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends AppCompatActivity {

    private final AtomicBoolean isServiceConnected = new AtomicBoolean(false);
    private DictionaryService dictionaryService;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            log("Entered onServiceConnected()");
            DictionaryService.LocalBinder binder = (DictionaryService.LocalBinder) service;
            dictionaryService = binder.getService();
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
        log("entered onCreate()");
        setContentView(R.layout.activity_main);
        setupFragmentsIf(savedInstanceState == null);
        setupDictionaryService();
    }


    public Optional<DictionaryService> getDictionaryService(){
        return Optional.ofNullable(dictionaryService);
    }


    private void setupDictionaryService() {
        Intent intent = new Intent(getApplicationContext(), DictionaryService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, connection, 0);
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
        System.out.println("^^^ MainActivity: " + msg);
    }

}