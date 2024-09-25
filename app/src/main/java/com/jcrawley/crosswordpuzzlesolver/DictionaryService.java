package com.jcrawley.crosswordpuzzlesolver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DictionaryService extends Service {

    IBinder binder = new LocalBinder();
    private MainActivity mainActivity;


    public DictionaryService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setMainActivity(MainActivity activity){
        this.mainActivity = activity;
    }

    public class LocalBinder extends Binder {
        public DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}