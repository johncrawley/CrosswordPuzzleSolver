package com.jcrawley.crosswordpuzzlesolver;

import java.util.List;

public interface WordListView {
    void setWords(List<String> words);
    void fadeOutList();
    void fadeInList();
}
