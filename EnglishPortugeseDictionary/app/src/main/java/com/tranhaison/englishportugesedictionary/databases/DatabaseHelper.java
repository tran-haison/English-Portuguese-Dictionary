package com.tranhaison.englishportugesedictionary.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.tranhaison.englishportugesedictionary.DictionaryWord;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database path and name
    private String DB_PATH;
    private static String DB_NAME  ="eng_dictionary.db";

    // Table name
    private static final String TABLE_WORDS = "words";
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_FAVORITE = "favorite";

    // Column name
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_DEFINITION = "definition";
    private static final String COLUMN_EXAMPLE = "example";
    private static final String COLUMN_SYNONYMS = "synonyms";
    private static final String COLUMN_ANTONYMS = "antonyms";

    // Init variables
    private SQLiteDatabase mDatabase;
    private final Context myContext;

    /**
     * Constructor - get context as parameter and init new database instance
     * Set the value of path equal to real path to database
     */
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        this.DB_PATH ="/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if database exists or not
     * @return
     */
    public boolean checkDatabase(){
        SQLiteDatabase check_db = null;

        try {
            String my_path = DB_PATH + DB_NAME;
            check_db = SQLiteDatabase.openDatabase(my_path,null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            e.printStackTrace();
        }

        if(check_db != null) {
            check_db.close();
        }

        return check_db != null ? true : false;
    }

    /**
     * Drop old database and create a new one
     */
    public void createDatabase() {
        boolean isDbExist = checkDatabase();

        // If database does not exist -> delete database file
        // then create a new one
        if (!isDbExist) {
            this.getReadableDatabase();
            try {
                myContext.deleteDatabase(DB_NAME);
                copyDatabase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Copy database from file .db to database in device
     * @throws IOException
     */
    private void copyDatabase() throws IOException {
        // Open database in assets folder
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Open database in device
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Init buffer to read database
        byte[] buffer = new byte[1024];
        int length;

        // Write from database file in assets to database file in device
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer,0, length);
        }

        // Finish
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Open database and set to mDatabase
     * @throws SQLException
     */
    public void openDatabase() throws SQLException {
        String my_path = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(my_path,null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Close database
     */
    @Override
    public synchronized void close() {
        if(mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }

    /**
     * Get a word from dictionary database
     * @param word
     * @return
     */
    public DictionaryWord getWord(String word) {
        // Remove all whitespaces
        word = word.replaceAll("\\s+","");

        /*Cursor cursor = mDatabase.rawQuery(
                "SELECT en_word, en_definition, example, synonyms, antonyms " +
                        "FROM words " +
                        "WHERE en_word == UPPER('" + word + "');"
                ,null);*/

        Cursor cursor = mDatabase.query(TABLE_WORDS,
                new String[] {COLUMN_WORD, COLUMN_DEFINITION, COLUMN_EXAMPLE, COLUMN_SYNONYMS, COLUMN_ANTONYMS},
                COLUMN_WORD + " = '" + word + "'",
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            DictionaryWord dictionaryWord = new DictionaryWord();
            dictionaryWord.setWord(cursor.getString(0));
            dictionaryWord.setDefinition(cursor.getString(1));
            dictionaryWord.setExample(cursor.getString(2));
            dictionaryWord.setSynonym(cursor.getString(3));
            dictionaryWord.setAntonym(cursor.getString(4));
            return dictionaryWord;
        } else {
            return null;
        }
    }

    /**
     * Get a list of suggested words
     * @param word
     * @return
     */
    public ArrayList<DictionaryWord> getSuggestions(String word) {
        // Remove all whitespaces
        word = word.replaceAll("\\s+","");

        /*Cursor cursor = mDatabase.rawQuery(
                "SELECT en_word, en_definition, example, synonyms, antonyms " +
                        "FROM words " +
                        "WHERE en_word LIKE '" + word + "%' " +
                        "LIMIT 40;",
                null);*/

        Cursor cursor = mDatabase.query(TABLE_WORDS,
                new String[] {COLUMN_WORD, COLUMN_DEFINITION, COLUMN_EXAMPLE, COLUMN_SYNONYMS, COLUMN_ANTONYMS},
                COLUMN_WORD + " LIKE '" + word + "%' ",
                null,
                null,
                null,
                null,
                String.valueOf(20));

        ArrayList<DictionaryWord> suggestionList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DictionaryWord dictionaryWord = new DictionaryWord();
                dictionaryWord.setWord(cursor.getString(0));
                dictionaryWord.setDefinition(cursor.getString(1));
                dictionaryWord.setExample(cursor.getString(2));
                dictionaryWord.setSynonym(cursor.getString(3));
                dictionaryWord.setAntonym(cursor.getString(4));
                suggestionList.add(dictionaryWord);
            } while (cursor.moveToNext());
        }

        return suggestionList;
    }

    /***************************************************
     * Code to modify and retrieve data in History table
     ***************************************************/

    /**
     * Put a word into History
     * @param dictionaryWord
     */
    public void insertHistory(DictionaryWord dictionaryWord) {
        String word = dictionaryWord.getWord();
        String definition = dictionaryWord.getDefinition();
        String example = dictionaryWord.getExample();
        String synonym = dictionaryWord.getSynonym();
        String antonym = dictionaryWord.getAntonym();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WORD, word);
        contentValues.put(COLUMN_DEFINITION, definition);
        contentValues.put(COLUMN_EXAMPLE, example);
        contentValues.put(COLUMN_SYNONYMS, synonym);
        contentValues.put(COLUMN_ANTONYMS, antonym);

        mDatabase.insert(TABLE_HISTORY, null, contentValues);
    }

    /**
     * Get a word from History
     * @param word
     * @return
     */
    public DictionaryWord getHistory(String word) {
        // Remove all whitespaces
        word = word.replaceAll("\\s+","");

        Cursor cursor = mDatabase.query(TABLE_HISTORY,
                new String[] {COLUMN_WORD, COLUMN_DEFINITION, COLUMN_EXAMPLE, COLUMN_SYNONYMS, COLUMN_ANTONYMS},
                COLUMN_WORD + " = " + word,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            DictionaryWord dictionaryWord = new DictionaryWord();
            dictionaryWord.setWord(cursor.getString(0));
            dictionaryWord.setDefinition(cursor.getString(1));
            dictionaryWord.setExample(cursor.getString(2));
            dictionaryWord.setSynonym(cursor.getString(3));
            dictionaryWord.setAntonym(cursor.getString(4));
            return dictionaryWord;
        } else {
            return null;
        }
    }

    /**
     * Get a list of all words in History
     * @return
     */
    public ArrayList<DictionaryWord> getAllHistory() {
        Cursor cursor = mDatabase.query(TABLE_HISTORY,
                new String[] {COLUMN_WORD, COLUMN_DEFINITION, COLUMN_EXAMPLE, COLUMN_SYNONYMS, COLUMN_ANTONYMS},
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
                dictionaryWord.setExample(cursor.getString(2));
                dictionaryWord.setSynonym(cursor.getString(3));
                dictionaryWord.setAntonym(cursor.getString(4));
                historyList.add(dictionaryWord);
            } while (cursor.moveToNext());
        }

        return historyList;
    }

    /**
     * Delete all words in History
     */
    public void deleteAllHistory() {
        mDatabase.delete(TABLE_HISTORY, null, null);
    }

    /****************************************************
     * Code to modify and retrieve data in Favorite table
     ****************************************************/

    /**
     * Put a word into Favorite
     * @param dictionaryWord
     */
    public void insertFavorite(DictionaryWord dictionaryWord) {
        String word = dictionaryWord.getWord();
        String definition = dictionaryWord.getDefinition();
        String example = dictionaryWord.getExample();
        String synonym = dictionaryWord.getSynonym();
        String antonym = dictionaryWord.getAntonym();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WORD, word);
        contentValues.put(COLUMN_DEFINITION, definition);
        contentValues.put(COLUMN_EXAMPLE, example);
        contentValues.put(COLUMN_SYNONYMS, synonym);
        contentValues.put(COLUMN_ANTONYMS, antonym);

        mDatabase.insert(TABLE_FAVORITE, null, contentValues);
    }

    /**
     * Get a word from Favorite
     * @param word
     * @return
     */
    public DictionaryWord getFavorite(String word) {
        // Remove all whitespaces
        word = word.replaceAll("\\s+","");

        Cursor cursor = mDatabase.query(TABLE_FAVORITE,
                new String[] {COLUMN_WORD, COLUMN_DEFINITION, COLUMN_EXAMPLE, COLUMN_SYNONYMS, COLUMN_ANTONYMS},
                COLUMN_WORD + " = '" + word + "'",
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            DictionaryWord dictionaryWord = new DictionaryWord();
            dictionaryWord.setWord(cursor.getString(0));
            dictionaryWord.setDefinition(cursor.getString(1));
            dictionaryWord.setExample(cursor.getString(2));
            dictionaryWord.setSynonym(cursor.getString(3));
            dictionaryWord.setAntonym(cursor.getString(4));
            return dictionaryWord;
        } else {
            return null;
        }
    }

    /**
     * Get a list of all words in Favorite
     * @return
     */
    public ArrayList<DictionaryWord> getAllFavorite() {
        Cursor cursor = mDatabase.query(TABLE_FAVORITE,
                new String[] {COLUMN_WORD, COLUMN_DEFINITION, COLUMN_EXAMPLE, COLUMN_SYNONYMS, COLUMN_ANTONYMS},
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
                dictionaryWord.setExample(cursor.getString(2));
                dictionaryWord.setSynonym(cursor.getString(3));
                dictionaryWord.setAntonym(cursor.getString(4));
                favoriteList.add(dictionaryWord);
            } while (cursor.moveToNext());
        }

        return favoriteList;
    }

    /**
     * Remove a word from Favorite
     * @param word
     */
    public void deleteFavorite(String word) {
        mDatabase.delete(TABLE_FAVORITE,
                COLUMN_WORD + " = " + word,
                null);
    }

    /**
     * Remove all words from Favorite
     */
    public void deleteAllFavorite() {
        mDatabase.delete(TABLE_FAVORITE, null, null);
    }
}
