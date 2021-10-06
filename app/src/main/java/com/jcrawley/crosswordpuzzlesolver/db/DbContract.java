package com.jcrawley.crosswordpuzzlesolver.db;

import android.provider.BaseColumns;

public final class DbContract {

    private DbContract(){}

    static class WordsEntry implements BaseColumns {
        static final String TABLE_NAME = "Words";
        static final String COL_CATEGORY_NAME = "word";
        static final String COL_CATEGORY_KEY = "key";
    }

}
