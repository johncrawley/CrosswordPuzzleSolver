package com.jcrawley.crosswordpuzzlesolver;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DictionaryLoader {

    private Context context;


    public DictionaryLoader(Context context){
        this.context = context;
    }


    String getAllWords(){
        String words = "";
        InputStream is = context.getResources().openRawResource(R.raw.british_english);
        StringBuilder str = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();

            while (line!= null){
                str.append(" ");
                str.append(line);
                line =br.readLine();
            }
            words = str.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return words;
    }
}
