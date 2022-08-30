package com.jcrawley.crosswordpuzzlesolver;

import com.jcrawley.crosswordpuzzlesolver.anagram.BinaryCounter;


public class BinaryCounterTest {

    public void testBinaryString(){
        BinaryCounter binaryCounter = new BinaryCounter(5);
        binaryCounter.getFlag();
        binaryCounter.inc();
        binaryCounter.getFlag();
        for(int i=0; i< 50; i++){
            binaryCounter.inc();
            binaryCounter.getFlag();
        }

    }

}
