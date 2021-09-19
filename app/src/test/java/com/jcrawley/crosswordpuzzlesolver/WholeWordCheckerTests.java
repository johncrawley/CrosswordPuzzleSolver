package com.jcrawley.crosswordpuzzlesolver;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WholeWordCheckerTests {

    WholeWordChecker wholeWordChecker;

    @Before
    public void init(){
        wholeWordChecker = new WholeWordChecker();
    }


    @Test
    public void canFindAddedWord(){
        String word = "hello";
        wholeWordChecker.addWord(word);
        assertTrue(wholeWordChecker.doesWordExist(word));
    }


    @Test
    public void imaginaryWordDoesntExist(){
        String word = "foooooo";
        assertFalse(wholeWordChecker.doesWordExist(word));
    }
}
