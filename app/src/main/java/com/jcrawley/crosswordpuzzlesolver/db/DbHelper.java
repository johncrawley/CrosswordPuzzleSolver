package com.jcrawley.crosswordpuzzlesolver.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jcrawley.crosswordpuzzlesolver.io.FileHandler;

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper instance;


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Words.db";

    private static final String OPENING_BRACKET = " (";
    private static final String CLOSING_BRACKET = " );";
    private static final  String INTEGER = " INTEGER";
    private static final String TEXT = " TEXT";
    private static final String COMMA = ",";
    public static final String UNIQUE = " UNIQUE ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";


    private static final String SQL_CREATE_WORDS_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS
                    + DbContract.WordsEntry.TABLE_NAME
                    + OPENING_BRACKET
                    + DbContract.WordsEntry._ID + INTEGER + PRIMARY_KEY + COMMA
                    + DbContract.WordsEntry.COL_WORD + TEXT
                    + DbContract.WordsEntry.COL_KEY_ID + INTEGER
                    + CLOSING_BRACKET;


    private static final String SQL_CREATE_KEYS_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS
                    + DbContract.WordKeyEntry.TABLE_NAME
                    + OPENING_BRACKET
                    + DbContract.WordKeyEntry._ID + INTEGER + PRIMARY_KEY + COMMA
                    + DbContract.WordKeyEntry.COL_KEY + TEXT
                    + CLOSING_BRACKET;


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbContract.WordsEntry.TABLE_NAME;

    private Context context;


    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    public static DbHelper getInstance(Context context){
        if(instance == null){
            instance = new DbHelper(context);
        }
        return instance;
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WORDS_TABLE);
        db.execSQL(SQL_CREATE_KEYS_TABLE);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}