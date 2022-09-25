package com.jcrawley.crosswordpuzzlesolver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class WordsRepositoryImpl implements WordsRepository {


    private final SQLiteDatabase db;


    public WordsRepositoryImpl(Context context){
        DbHelper dbHelper = DbHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }


    public void addWord(List<String> words, String key) {
        long keyId = saveWordKey(key);
        if(keyId == -1){
            return;
        }
        for(String word : words){
            saveWord(keyId, word);
        }
    }


    long saveWordKey(String key) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.WordKeyEntry.COL_KEY, key);
        return addValuesToTable(db, DbContract.WordKeyEntry.TABLE_NAME, contentValues);
    }


    long saveWord(long keyId, String word) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.WordsEntry.COL_WORD, word);
        contentValues.put(DbContract.WordsEntry.COL_KEY_ID, keyId);
        return addValuesToTable(db, DbContract.WordKeyEntry.TABLE_NAME, contentValues);
    }


    static long addValuesToTable(SQLiteDatabase db, String tableName, ContentValues contentValues){
        db.beginTransaction();
        long id = -1;
        try {
            id = db.insertOrThrow(tableName, null, contentValues);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
        }
        db.endTransaction();
        return id;
    }

    @Override
    public void addWord(String word, String key) {

    }

    @Override
    public List<String> getWords(String key) {
        return null;
    }
}
