package com.jcrawley.crosswordpuzzlesolver.dictionary.anagram;


public class BinaryCounter {

    private int size;
    private int currentIndex;
    private String maxIndexString;
    private boolean isIndexAtLimit;


    public BinaryCounter(int size){
        init(size);
    }


    public void init(int size){
        this.size = size;
        this.isIndexAtLimit = false;
        currentIndex = 0;
        generateMaxIndexString();
    }


    public void inc(){
        if(!isIndexAtLimit){
            currentIndex++;
            calculateIfIndexIsAtLimit();
        }
    }


    public boolean isIndexAtLimit(){
        return this.isIndexAtLimit;
    }


    private void calculateIfIndexIsAtLimit(){
        isIndexAtLimit = Integer.toBinaryString(currentIndex).equals(maxIndexString);
    }


    public String getFlag(){
        var str = new StringBuilder();
        String binaryStr = Integer.toBinaryString(currentIndex);
        int numberOfZerosToPrefix = size - binaryStr.length();
        for(int i=0; i < numberOfZerosToPrefix; i++){
            str.append("0");
        }
        str.append(binaryStr);
        return str.toString();
    }


    public void generateMaxIndexString(){
        StringBuilder str=  new StringBuilder();
        for(int i = 0; i < size; i++){
            str.append("1");
        }
        maxIndexString = str.toString();
    }

}
