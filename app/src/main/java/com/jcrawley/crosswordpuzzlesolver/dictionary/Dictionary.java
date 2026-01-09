package com.jcrawley.crosswordpuzzlesolver.dictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record Dictionary(
        String wordsStr,
        Map<String, Set<String>> wordsMap,
        Map<Integer, Map<String, Set<String>>> wordsByLengthMap,
        List<String> wordsList) {


}
