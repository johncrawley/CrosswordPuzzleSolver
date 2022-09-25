package com.jcrawley.crosswordpuzzlesolver.db;

import android.provider.BaseColumns;

public final class DbContract {

    private DbContract(){}

    static class WordsEntry implements BaseColumns {
        static final String TABLE_NAME = "Words";
        static final String COL_WORD = "word";
        static final String COL_KEY_ID = "key_id";
    }


    static class WordKeyEntry implements BaseColumns {
        static final String TABLE_NAME = "Keys";
        static final String COL_KEY = "key";
    }

}
