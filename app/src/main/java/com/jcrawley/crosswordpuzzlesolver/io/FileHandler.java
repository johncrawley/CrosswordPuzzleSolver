package com.jcrawley.crosswordpuzzlesolver.io;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FileHandler {

    private final File baseDirectory;
    private final Context context;
    private String filePath;
    private final String filename = "wordMap.dat";

    public FileHandler(Context context){
        this.context = context;
        final String DIRECTORY_NAME = "fileDir";
        baseDirectory = new File(context.getFilesDir(), DIRECTORY_NAME);
    }



    private void createFile(String filename){
        FileOutputStream outputStream = null;
        byte [] bytes = new byte[]{};
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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



    public void writeFileOnInternalStorage(String categoryName, String filename, String str){

        String fullFilename = getFullFilename(categoryName, filename);

        createFile(fullFilename);
        filePath = context.getApplicationContext().getFilesDir() + fullFilename;
        if(!couldNotCreateBaseDirectory()){
            return;
        }
        try {
            FileOutputStream fileOutputStream = context.getApplicationContext().openFileOutput(fullFilename,Context.MODE_PRIVATE);
            //FileWriter writer = new FileWriter(filePath);

            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.write(str);
            writer.flush();
            writer.flush();
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getFullFilename(String categoryName, String filename){
        String EXTENSION = ".txt";
        return categoryName + "__" + filename  + EXTENSION;
    }


    public String readFileFromInternalStorage(String categoryName, String filename){

        StringBuilder text = new StringBuilder();
        try {
            filePath = context.getFilesDir() + getFullFilename(categoryName, filename);
            printFileInfo(filePath);
            // FileInputStream fileInputStream = context.getApplicationContext().openFileInput(filePath);
            FileReader fileReaderold = new FileReader(new File(filePath));
            FileReader fileReader = new FileReader(new File(getFullFilename(categoryName, filename)));
            //InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader buffer = new BufferedReader(fileReader);
            String line;
            while ((line = buffer.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            buffer.close();
        } catch (IOException e) {
            System.out.println("^^^ Error! occured while reading text file from Internal Storage!");
            e.printStackTrace();
        }
        return text.toString();
    }



    String fileContents = "Hello world!";

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


    public void saveFileContents(){
        fileContents += " " + System.currentTimeMillis();
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private  void printFileInfo(String filePath){
        File file = new File(filePath);
        System.out.println("^^^ Filepath: " + filePath + " exists? : " + file.exists());
    }


    private boolean couldNotCreateBaseDirectory(){
        if(baseDirectory.exists()){
            return !baseDirectory.mkdir();
        }
        return false;
    }

}