package com.jcrawley.crosswordpuzzlesolver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jcrawley.crosswordpuzzlesolver.dictionary.DictionaryLoader;
import com.jcrawley.crosswordpuzzlesolver.io.FileHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordsRepositoryImpl implements WordsRepository {


    private final SQLiteDatabase db;
    private final Context context;


    public WordsRepositoryImpl(Context context){
        this.context = context;
        DbHelper dbHelper = DbHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }


    @Override
    public void saveWordsFromMapFile(){
        FileHandler fileHandler = new FileHandler(context);
        System.out.println("WordsRepositoryImpl.saveWordsFromMapFile() about to run fileHandler parser");
        fileHandler.processWordsFromMapFile(this::parseSavedWordsList);
    }

    @Override
    public void saveWordsFromDictionary(DictionaryLoader dictionaryLoader){
        dictionaryLoader.loadWordsIntoDb(this::parseSavedWordsList);
    }

    @Override
    public Set<String> getAllWords(){
        Set<String> foundWords = new HashSet<>(50000);
        String query = "SELECT * FROM "
                + DbContract.WordsEntry.TABLE_NAME + ";";

        try(Cursor cursor= db.rawQuery(query, null)){
            while(cursor.moveToNext()){
                foundWords.add(getStr(cursor, DbContract.WordsEntry.COL_WORD));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("Found All words count : " + foundWords.size());
        return foundWords;
    }


    @Override
    public boolean hasAnyWords(){
        Set<String> foundWords = new HashSet<>();
        String query = "SELECT _ID FROM " + DbContract.WordsEntry.TABLE_NAME + " LIMIT 1;";

        try(Cursor cursor= db.rawQuery(query, null)){
            while(cursor.moveToNext()){
                foundWords.add(getStr(cursor, DbContract.WordsEntry._ID));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("Found All words count : " + foundWords.size());
        return !foundWords.isEmpty();
    }


    public Set<String> findWordsWithKey(String letters){
        Set<String> foundWords = new HashSet<>(500);
        String query = "SELECT * FROM Words INNER JOIN Keys ON Keys._ID = Words.key_id WHERE " + DbContract.WordKeyEntry.COL_KEY + " =?;";

        try(Cursor cursor= db.rawQuery(query, new String[]{"%" + letters + "%"})){
            while(cursor.moveToNext()){
                foundWords.add(getStr(cursor, DbContract.WordsEntry.COL_WORD));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("Found words count : " + foundWords.size());
        return foundWords;
    }


    private void parseSavedWordsList(String line){
        String delimiter = " ";
        String[] wordsArray = line.split(delimiter);
        String key = wordsArray[0];
        long keyId = saveWordKey(key);
        for(int i=1; i< wordsArray.length;i++){
            saveWord(keyId, wordsArray[i]);
        }
    }


    private  static String getStr(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
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
        return addValuesToTable(db, DbContract.WordsEntry.TABLE_NAME, contentValues);
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
