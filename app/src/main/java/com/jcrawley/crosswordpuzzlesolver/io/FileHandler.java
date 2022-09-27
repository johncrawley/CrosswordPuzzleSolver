package com.jcrawley.crosswordpuzzlesolver.io;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class FileHandler {

    private final Context context;
    private final String filename = "sorted_british_english.txt";

    public FileHandler(Context context){
        this.context = context;
    }


    public boolean doesFileExist(){
       return new File(context.getFilesDir(), filename).exists();
    }


    public void writeWordsToFile(List<String> wordsWithKeys){
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {

            for(String str : wordsWithKeys){
                fos.write(str.getBytes());
                fos.write("\n".getBytes());
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> loadWordEntriesFromFile(){
        File file = new File(context.getFilesDir(), filename);
        List<String> wordsEntries = new ArrayList<>(130_000);

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(fileReader);
            String line;
            while ((line = buffer.readLine()) != null) {
                wordsEntries.add(line);
            }
            buffer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return wordsEntries;
    }


    public void processWordsFromMapFile(Consumer<String> lineConsumer){
        File file = new File(context.getFilesDir(), filename);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(fileReader);
            String line;
            while ((line = buffer.readLine()) != null) {
                lineConsumer.accept(line);
            }
            buffer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public String getFileContents(){
        File file = new File(context.getFilesDir(), filename);
        StringBuilder str = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(fileReader);
            String line;
            while ((line = buffer.readLine()) != null) {
                str.append(line);
                str.append('\n');
            }
            buffer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return str.toString();
    }


}