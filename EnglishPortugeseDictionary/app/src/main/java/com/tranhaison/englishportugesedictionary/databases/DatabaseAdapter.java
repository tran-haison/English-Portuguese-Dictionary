package com.tranhaison.englishportugesedictionary.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tranhaison.englishportugesedictionary.DictionaryWord;

import java.util.ArrayList;

public class DatabaseAdapter {

    // Database name and version
    private static final String DATABASE_NAME = "dictionary_database";
    private static final int DATABASE_VERSION = 2;

    // Tables name
    private static final String TABLE_FAVORITE = "favorite";
    private static final String TABLE_HISTORY = "history";

    // Column name of Favorite table
    private static final String KEY_FAVORITE_WORD = "word";
    private static final String KEY_FAVORITE_CONTENT = "content";

    // Column name of History table
    private static final String KEY_HISTORY_WORD ="word";
    private static final String KEY_HISTORY_CONTENT ="content";

    // Init global variables
    private final Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    // Create Favorite table
    private static final String CREATE_TABLE_FAVORITE =
            "CREATE TABLE " + TABLE_FAVORITE + " ("
                    + KEY_FAVORITE_WORD + " TEXT PRIMARY KEY, "
                    + KEY_FAVORITE_CONTENT + " TEXT NOT NULL);";

    // Create History table
    private static final String CREATE_TABLE_HISTORY =
            "CREATE TABLE " + TABLE_HISTORY + " ("
                    + KEY_HISTORY_WORD + " TEXT PRIMARY KEY, "
                    + KEY_HISTORY_CONTENT + " TEXT NOT NULL);";

    /**
     * Database Helper class to create database and tables
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_FAVORITE);
            sqLiteDatabase.execSQL(CREATE_TABLE_HISTORY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
            onCreate(sqLiteDatabase);
        }
    }

    /**
     * Constructor - take the context to allow database to be opened/closed
     * @param context
     */
    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, signal the failure
     * @return
     * @throws SQLException
     */
    public DatabaseAdapter openDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close database
     */
    public void closeDatabase() {
        databaseHelper.close();
    }

    /**
     * Add a word to table Favorite
     * @param dictionaryWord
     * @return
     */
    public long addWordToFavorite(DictionaryWord dictionaryWord) {
        String word = dictionaryWord.getWord();
        String content = dictionaryWord.getDefinition();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FAVORITE_WORD, word);
        contentValues.put(KEY_FAVORITE_CONTENT, content);
        return database.insert(TABLE_FAVORITE, null, contentValues);
    }

    /**
     * Remove a word from table Favorite
     * @param dictionaryWord
     * @return true if a word is removed successfully, false otherwise
     */
    public boolean removeWordFromFavorite(DictionaryWord dictionaryWord) {
        String word = dictionaryWord.getWord();

        int isRemoved = database.delete(
                TABLE_FAVORITE,
                KEY_FAVORITE_WORD + " = " + word,
                null);

        return isRemoved > 0;
    }

    /**
     * Remove all words from table Favorite
     * @return
     */
    public boolean removeAllWordsFromFavorite() {
        int isRemoved = database.delete(
                TABLE_FAVORITE,
                null,
                null
        );
        return isRemoved > 0;
    }

    /**
     * Get a word from Favorite table
     * @param word
     * @return
     */
    public DictionaryWord getWordFromFavorite(String word) {
        Cursor cursor = database.query(
                TABLE_FAVORITE,
                new String[] {KEY_FAVORITE_WORD, KEY_FAVORITE_CONTENT},
                KEY_FAVORITE_WORD + " = " + word,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();

            DictionaryWord dictionaryWord = new DictionaryWord();
            dictionaryWord.setWord(cursor.getString(0));
            dictionaryWord.setDefinition(cursor.getString(1));

            return dictionaryWord;
        } else {
            return null;
        }
    }

    /**
     * Get all words from Favorite table
     * @return
     */
    public ArrayList<DictionaryWord> getAllWordsFromFavorite() {
        Cursor cursor = database.query(
                TABLE_FAVORITE,
                new String[] {KEY_FAVORITE_WORD, KEY_FAVORITE_CONTENT},
                null,
                null,
                null,
                null,
                null);

        ArrayList<DictionaryWord> favoriteList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DictionaryWord dictionaryWord = new DictionaryWord();
                dictionaryWord.setWord(cursor.getString(0));
                dictionaryWord.setDefinition(cursor.getString(1));
                favoriteList.add(dictionaryWord);
            } while (cursor.moveToNext());
        }

        return favoriteList;
    }

    /**
     * Add a word to table History
     * @param dictionaryWord
     * @return
     */
    public long addWordToHistory(DictionaryWord dictionaryWord) {
        String word = dictionaryWord.getWord();
        String content = dictionaryWord.getDefinition();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_HISTORY_WORD, word);
        contentValues.put(KEY_HISTORY_CONTENT, content);
        return database.insert(TABLE_HISTORY, null, contentValues);
    }

    /**
     * Remove a word from table History
     * @param dictionaryWord
     * @return true if a word is removed successfully, false otherwise
     */
    public boolean removeWordFromHistory(DictionaryWord dictionaryWord) {
        String word = dictionaryWord.getWord();

        int isRemoved = database.delete(
                TABLE_HISTORY,
                KEY_HISTORY_WORD + " = " + word,
                null);

        return isRemoved > 0;
    }

    /**
     * Remove all words from table History
     * @return
     */
    public boolean removeAllWordsFromHistory() {
        int isRemoved = database.delete(
                TABLE_HISTORY,
                null,
                null
        );
        return isRemoved > 0;
    }

    /**
     * Get a word from History table
     * @param word
     * @return
     */
    public DictionaryWord getWordFromHistory(String word) {
        Cursor cursor = database.query(
                TABLE_HISTORY,
                new String[] {KEY_HISTORY_WORD, KEY_HISTORY_CONTENT},
                KEY_HISTORY_WORD + " = " + word,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();

            DictionaryWord dictionaryWord = new DictionaryWord();
            dictionaryWord.setWord(cursor.getString(0));
            dictionaryWord.setDefinition(cursor.getString(1));

            return dictionaryWord;
        } else {
            return null;
        }
    }

    /**
     * Get all words from History table
     * @return
     */
    public ArrayList<DictionaryWord> getAllWordsFromHistory() {
        Cursor cursor = database.query(
                TABLE_HISTORY,
                new String[] {KEY_HISTORY_WORD, KEY_HISTORY_CONTENT},
                null,
                null,
                null,
                null,
                null);

        ArrayList<DictionaryWord> historyList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DictionaryWord dictionaryWord = new DictionaryWord();
                dictionaryWord.setWord(cursor.getString(0));
                dictionaryWord.setDefinition(cursor.getString(1));
                historyList.add(dictionaryWord);
            } while (cursor.moveToNext());
        }

        return historyList;
    }

}
