package com.jcrawley.crosswordpuzzlesolver.anagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnagramFinder {

    private final BinaryCounter binaryCounter;

    public AnagramFinder(){
        this.binaryCounter = new BinaryCounter(2);
    }

    public List<String> getWordsFrom(String letters){

        List<String> foundWords = new ArrayList<>();
        binaryCounter.init(letters.length());
        String flag = binaryCounter.getFlag();
        String searchLetters = "";

        for(int i=0; i< letters.length();i++){
            if(flag.charAt(i)== 1){
                searchLetters += letters.charAt(i);
            }
        }


        List<String> lettersList = Arrays.asList(letters.split(""));
        List<Boolean> isUsed = new ArrayList<>(lettersList.size());
        int maxIndex = lettersList.size();
        for(int i=0; i < maxIndex; i++){
            isUsed.add(true);
        }

    return null;

    }


    private boolean[] getFlagArray(int size){
        boolean[] flagArray = new boolean[size];
        for(int i=0; i< size; i++){
            flagArray[i] = false;
        }
        return flagArray;
    }




}
