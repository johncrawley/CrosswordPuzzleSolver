package com.jcrawley.crosswordpuzzlesolver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoaderImpl;

import java.util.concurrent.Executors;

public class DictionaryService extends Service {

    IBinder binder = new LocalBinder();
    private MainActivity mainActivity;
    private DictionaryLoader dictionaryLoader;


    public DictionaryService() {
        loadDictionaryWords();
    }


    private void loadDictionaryWords(){
        Executors.newSingleThreadExecutor().submit(
                ()->  dictionaryLoader = new DictionaryLoaderImpl(getApplicationContext()));
    }


    public DictionaryLoader getDictionaryLoader(){
        return dictionaryLoader;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void setActivity(MainActivity activity){
        this.mainActivity = activity;
    }


    public class LocalBinder extends Binder {
        public DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}