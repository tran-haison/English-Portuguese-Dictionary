package com.tranhaison.englishportugesedictionary.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.tranhaison.englishportugesedictionary.databases.utils.StringDecrypter;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.examples.ExampleSentence;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.EnglishDictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.PortugueseDictionaryWord;
import com.tranhaison.englishportugesedictionary.utils.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database path and name
    private String DB_PATH;
    private static String DB_NAME = "dictionary.db";

    // Delimiters
    public static final String DEF_DELIMITER = "\u0003";
    public static final String POS_DELIMITER = "\u0002";

    // Table name
    public static final String TABLE_ENGLISH_WORD = "engWordList";
    public static final String TABLE_PORTUGUESE_WORD = "porWordList";
    public static final String TABLE_INDEX_WORD = "indexWordList";
    public static final String TABLE_EXAMPLE = "exampleList";
    public static final String TABLE_REFERENCE_INDEX_WORD = "ref_indexWordList";
    public static final String TABLE_SENTENCE = "sentenceList";
    public static final String TABLE_RELATION = "relationList";
    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_FAVORITE = "favorite";

    /************************************************************
     * Attributes of table engWordList and porWordList *
     ************************************************************/

    // General attributes
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DISPLAY_WORD = "displayWord";
    public static final String COLUMN_POS_TYPE = "posTypes";
    public static final String COLUMN_EXPLANATION = "explanations";

    // Table engWordList
    public static final String COLUMN_US_PHONETIC = "US_phonetic";
    public static final String COLUMN_UK_PHONETIC = "UK_phonetic";
    public static final String COLUMN_ENG_POS_TYPES = "engPosTypes";
    public static final String COLUMN_ENG_EXPLANATION = "engExplanations";
    public static final String COLUMN_GRAMMAR_TYPES = "grammarTypes";
    public static final String COLUMN_GRAMMARS = "grammars";

    /***************************************
     * Attributes of other tables *
     ***************************************/

    // General attributes
    public static final String COLUMN_WORD_LIST_ID = "wordList_id";
    public static final String COLUMN_INDEX_WORD_ID = "indexWordList_id";
    public static final String COLUMN_DICTIONARY_TYPE = "dictionary_type";

    // Table exampleList
    public static final String COLUMN_SENTENCE_LIST_ID = "sentenceList_id";

    // Table indexWordList
    public static final String COLUMN_INDEX_WORD = "indexWord";

    // Table relationList
    public static final String COLUMN_POR_WORD_LIST_ID = "porWordList_id";

    // Table sentenceList
    public static final String COLUMN_ENGLISH_SENTENCES = "english_sentences";
    public static final String COLUMN_TRANSLATION = "translation";

    // Init global variables
    private SQLiteDatabase mDatabase;
    private final Context myContext;

    /**
     * Constructor - get context as parameter and init new database instance
     * Set the value of path equal to real path to database
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }

    /****************************************************
     * Code to create, upgrade, open and close database *
     ****************************************************/

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if database already exists or not
     *
     * @return
     */
    public boolean checkDatabase() {
        SQLiteDatabase check_db = null;

        try {
            String my_path = DB_PATH + DB_NAME;
            check_db = SQLiteDatabase.openDatabase(my_path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (check_db != null) {
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
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Copy database from file .db to database in device
     *
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
            myOutput.write(buffer, 0, length);
        }

        // Finish copying
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Open database and set to mDatabase
     *
     * @throws SQLException
     */
    public void openDatabase() throws SQLException {
        String my_path = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(my_path, null, SQLiteDatabase.OPEN_READWRITE);

        // Case sensitive with LIKE
        String sql = "PRAGMA case_sensitive_like = true;";
        mDatabase.rawQuery(sql, null);
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }

    /*************************************
     * Code to retrieve data in database *
     *************************************/

    /**
     * Get specific word and all of its related information
     *
     * @param wordList_id
     * @return
     */
    public DictionaryWord getWord(int wordList_id, int dictionary_type) {
        if (dictionary_type == Constants.POR_ENG) {
            return getPorWord(wordList_id);
        } else {
            return getEngWord(wordList_id);
        }
    }

    /**
     * Return an english word and all its information
     *
     * @param wordList_id
     * @return
     */
    public EnglishDictionaryWord getEngWord(int wordList_id) {
        EnglishDictionaryWord dictionaryWord = new EnglishDictionaryWord();

        // Cursor to retrieve a record in engWordList table
        Cursor cursor = mDatabase.query(TABLE_ENGLISH_WORD,
                new String[]{COLUMN_ID, COLUMN_DISPLAY_WORD,
                        COLUMN_US_PHONETIC, COLUMN_UK_PHONETIC,
                        COLUMN_POS_TYPE, COLUMN_EXPLANATION,
                        COLUMN_ENG_POS_TYPES, COLUMN_ENG_EXPLANATION,
                        COLUMN_GRAMMAR_TYPES, COLUMN_GRAMMARS},
                COLUMN_ID + " = '" + wordList_id + "'",
                null,
                null,
                null,
                null);

        // Set attributes
        if (cursor != null && cursor.moveToFirst()) {
            dictionaryWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
            dictionaryWord.setDisplayWord(cursor.getString(1));
            dictionaryWord.setUS_phonetic(StringDecrypter.decrypt(cursor.getString(2)));
            dictionaryWord.setUK_phonetic(StringDecrypter.decrypt(cursor.getString(3)));
            dictionaryWord.setPosTypes(cursor.getString(4));
            dictionaryWord.setExplanations(cursor.getString(5));
            dictionaryWord.setEngPosTypes(cursor.getString(6));
            dictionaryWord.setEngExplanations(cursor.getString(7));
            dictionaryWord.setGrammarTypes(cursor.getString(8));
            dictionaryWord.setGrammars(cursor.getString(9));

            cursor.close();
            return dictionaryWord;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * Return a portuguese word and all its information
     *
     * @param wordList_id
     * @return
     */
    public PortugueseDictionaryWord getPorWord(int wordList_id) {
        PortugueseDictionaryWord dictionaryWord = new PortugueseDictionaryWord();

        // Cursor to retrieve a record in porWordList table
        Cursor cursor = mDatabase.query(TABLE_PORTUGUESE_WORD,
                new String[]{COLUMN_ID, COLUMN_DISPLAY_WORD,
                        COLUMN_POS_TYPE, COLUMN_EXPLANATION,},
                COLUMN_ID + " = '" + wordList_id + "'",
                null,
                null,
                null,
                null);

        // Set attributes
        if (cursor != null && cursor.moveToFirst()) {
            dictionaryWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
            dictionaryWord.setDisplayWord(cursor.getString(1));
            dictionaryWord.setPosTypes(cursor.getString(2));
            dictionaryWord.setExplanations(cursor.getString(3));

            cursor.close();
            return dictionaryWord;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * Get the definition of a particular word and split into different strings
     *
     * @param wordList_id
     * @param dictionary_type
     * @return
     */
    public ArrayList<String> getDefinitionForWord(int wordList_id, int dictionary_type) {
        ArrayList<String> definitionList = new ArrayList<>();

        // Get sql depend on dictionary_type
        String sql;
        if (dictionary_type == Constants.POR_ENG) {
            sql = "SELECT " + COLUMN_POS_TYPE + ", " + COLUMN_EXPLANATION +
                    " FROM " + TABLE_PORTUGUESE_WORD +
                    " WHERE " + COLUMN_ID + " = " + wordList_id + ";";
        } else {
            sql = "SELECT " + COLUMN_POS_TYPE + ", " + COLUMN_EXPLANATION +
                    " FROM " + TABLE_ENGLISH_WORD +
                    " WHERE " + COLUMN_ID + " = " + wordList_id + ";";
        }

        Cursor rawQuery = mDatabase.rawQuery(sql, null);
        if (rawQuery != null) {
            if (rawQuery.getCount() > 0 && rawQuery.moveToFirst()) {
                do {
                    // Get the posTypes and explanations of the word
                    String posType = rawQuery.getString(0);
                    String explanation = rawQuery.getString(1);

                    if (explanation.trim().length() > 0 && posType.length() > 0) {
                        // Split the posType and explanation by delimiter
                        // Put parts into array
                        String[] split = explanation.split(POS_DELIMITER);
                        String[] split2 = posType.split(POS_DELIMITER);

                        if (split.length == split2.length) {
                            for (int i2 = 0; i2 < split2.length; i2++) {
                                // Get the current part of the array
                                String trim = split[i2].trim();
                                String trim2 = split2[i2].trim();

                                if (!(trim.length() == 0 || trim2.length() == 0)) {
                                    // Split the explanation by its delimiter
                                    String[] split3 = trim.split(DEF_DELIMITER);

                                    for (int i3 = 0; i3 < split3.length; i3++) {
                                        String str2 = split3[i3];

                                        // Add to array list of string
                                        if (!definitionList.contains(str2)) {
                                            definitionList.add(str2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } while (rawQuery.moveToNext());
            }
            rawQuery.close();
        }
        return definitionList;
    }

    /**
     * Get all word types of a word
     * Example: benefit -> benefited, benefiting, ...
     *
     * @param wordList_id
     * @return
     */
    public ArrayList<String> getGrammars(int wordList_id) {
        // Cursor to get all indexWordList_id of a word from table ref_indexWordList
        Cursor ref_cursor = mDatabase.query(TABLE_REFERENCE_INDEX_WORD,
                new String[]{COLUMN_INDEX_WORD_ID},
                COLUMN_WORD_LIST_ID + " = '" + wordList_id + "'",
                null,
                null,
                null,
                null);

        ArrayList<String> grammarList = new ArrayList<>();

        // For each indexWordList_id -> get its corresponding indexWord
        if (ref_cursor != null && ref_cursor.moveToFirst()) {
            do {
                int indexWordList_id = Integer.parseInt(ref_cursor.getString(0));

                // Cursor to get indexWord from table indexWordList
                Cursor index_cursor = mDatabase.query(TABLE_INDEX_WORD,
                        new String[]{COLUMN_INDEX_WORD},
                        COLUMN_ID + " = '" + indexWordList_id + "'",
                        null,
                        null,
                        null,
                        null);

                // Put indexWord to array list
                if (index_cursor != null && index_cursor.moveToFirst()) {
                    String indexWord = index_cursor.getString(0);
                    grammarList.add(indexWord);
                }
            } while (ref_cursor.moveToNext());
        }
        ref_cursor.close();

        // By default, all words (both Eng & Por) have at least 1 indexWord
        // So if wordTypeList has size of 1, means there is no other word type of that word -> return null
        // else return an array list of all types of that word
        if (grammarList.size() <= 1) {
            return null;
        } else {
            return grammarList;
        }
    }

    /**
     * Get English explanation of a word
     *
     * @param wordList_id
     * @return
     */
    public ArrayList<String> getEnglishExplanation(int wordList_id) {
        ArrayList<String> engExplanationList = new ArrayList<>();

        Cursor rawQuery = mDatabase.rawQuery("SELECT " + COLUMN_ENG_POS_TYPES + ", " + COLUMN_ENG_EXPLANATION
                        + " FROM " + TABLE_ENGLISH_WORD
                        + " WHERE " + COLUMN_ID + " = " + wordList_id + ";",
                null);

        if (rawQuery != null && rawQuery.getCount() > 0 && rawQuery.moveToFirst()) {
            do {
                String engExplanation = rawQuery.getString(1);
                String engPosType = rawQuery.getString(0);

                if (engExplanation.trim().length() > 0 && engPosType.length() > 0) {
                    String[] split = engExplanation.split(POS_DELIMITER);
                    String[] split2 = engPosType.split(POS_DELIMITER);

                    if (split.length == split2.length) {
                        for (int i2 = 0; i2 < split2.length; i2++) {
                            String trim = split2[i2].trim();
                            String trim2 = split[i2].trim();

                            if (!(trim.length() == 0 || trim2.length() == 0)) {
                                String[] split3 = trim2.split(DEF_DELIMITER);

                                for (String str : split3) {
                                    if (!engExplanationList.contains(str)) {
                                        engExplanationList.add(str);
                                    }
                                }
                            }
                        }
                    }
                }
            } while (rawQuery.moveToNext());
            rawQuery.close();
            return engExplanationList;
        } else {
            rawQuery.close();
            return null;
        }
    }

    /**
     * Get all related words (Portuguese) of an English word
     *
     * @param wordList_id
     * @return
     */
    public ArrayList<DictionaryWord> getPorRelatedWord(int wordList_id) {
        // Cursor to get all porWordList_id of English wordList_id from table relationList
        Cursor relation_cursor = mDatabase.query(TABLE_RELATION,
                new String[]{COLUMN_POR_WORD_LIST_ID},
                COLUMN_WORD_LIST_ID + " ='" + wordList_id + "'",
                null,
                null,
                null,
                null);

        ArrayList<DictionaryWord> relatedWordsList = new ArrayList<>();

        // For each porWordList_id -> get its record from porWordList table
        if (relation_cursor != null && relation_cursor.moveToFirst()) {
            do {
                int porWordList_id = Integer.parseInt(relation_cursor.getString(0));

                DictionaryWord portugueseWord = new DictionaryWord();

                // Cursor to retrieve a record in porWordList table
                Cursor por_cursor = mDatabase.query(TABLE_PORTUGUESE_WORD,
                        new String[]{COLUMN_ID, COLUMN_DISPLAY_WORD,
                                COLUMN_POS_TYPE, COLUMN_EXPLANATION},
                        COLUMN_ID + " = '" + porWordList_id + "'",
                        null,
                        null,
                        null,
                        null);

                // Set attributes
                if (por_cursor != null && por_cursor.moveToFirst()) {
                    portugueseWord.setWordList_id(Integer.parseInt(por_cursor.getString(0)));
                    portugueseWord.setDisplayWord(por_cursor.getString(1));
                    portugueseWord.setPosTypes(por_cursor.getString(2));
                    portugueseWord.setExplanations(por_cursor.getString(3));
                    relatedWordsList.add(portugueseWord);

                    por_cursor.close();
                }
            } while (relation_cursor.moveToNext());

            relation_cursor.close();
            return relatedWordsList;
        } else {
            relation_cursor.close();
            return null;
        }
    }

    /**
     * Get all related words (English) of a Portuguese word
     *
     * @param porWordList_id
     * @return
     */
    public ArrayList<DictionaryWord> getEngRelatedWord(int porWordList_id) {
        // Cursor to get all porWordList_id of English wordList_id from table relationList
        Cursor relation_cursor = mDatabase.query(TABLE_RELATION,
                new String[]{COLUMN_WORD_LIST_ID},
                COLUMN_POR_WORD_LIST_ID + " ='" + porWordList_id + "'",
                null,
                null,
                null,
                null);

        ArrayList<DictionaryWord> engRelatedList = new ArrayList<>();

        if (relation_cursor != null && relation_cursor.moveToFirst()) {
            do {
                int wordList_id = Integer.parseInt(relation_cursor.getString(0));

                DictionaryWord englishDictionaryWord = new DictionaryWord();

                // Cursor to retrieve a record in porWordList table
                Cursor eng_cursor = mDatabase.query(TABLE_ENGLISH_WORD,
                        new String[]{COLUMN_ID, COLUMN_DISPLAY_WORD,
                                COLUMN_POS_TYPE, COLUMN_EXPLANATION},
                        COLUMN_ID + " = '" + wordList_id + "'",
                        null,
                        null,
                        null,
                        null);

                if (eng_cursor != null && eng_cursor.moveToFirst()) {
                    englishDictionaryWord.setWordList_id(Integer.parseInt(eng_cursor.getString(0)));
                    englishDictionaryWord.setDisplayWord(eng_cursor.getString(1));
                    englishDictionaryWord.setPosTypes(eng_cursor.getString(2));
                    englishDictionaryWord.setExplanations(eng_cursor.getString(3));
                    engRelatedList.add(englishDictionaryWord);

                    eng_cursor.close();
                }
            } while (relation_cursor.moveToNext());

            relation_cursor.close();
            return engRelatedList;
        } else {
            relation_cursor.close();
            return null;
        }
    }

    /**
     * Get wordList_id of a word
     *
     * @param word
     * @return
     */
    public int getWordListId(String word, int dictionary_type) {
        // Set default value to engWordList table
        String table_type = TABLE_ENGLISH_WORD;

        if (dictionary_type == Constants.ENG_POR) {
            table_type = TABLE_ENGLISH_WORD;
        } else if (dictionary_type == Constants.POR_ENG) {
            table_type = TABLE_PORTUGUESE_WORD;
        }

        /*if (word.contains("'")) {
            word.replace("'", "''");
        }*/

        // Cursor to get indexWordList_id from indexWordList table
        Cursor word_cursor = null;
        try {
            word_cursor = mDatabase.query(table_type,
                    new String[]{COLUMN_ID},
                    COLUMN_DISPLAY_WORD + " = '" + word + "'",
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (word_cursor != null && word_cursor.moveToFirst()) {
            int wordList_id = Integer.parseInt(word_cursor.getString(0));

            word_cursor.close();
            return wordList_id;
        } else {
            return -1;
        }
    }

    /**
     * Get a list of all examples of particular word
     *
     * @param wordList_id
     * @return
     */
    public ArrayList<ExampleSentence> getExamples(int wordList_id) {
        // Cursor to get all sentenceList_id of a word in exampleList table
        Cursor example_cursor = mDatabase.query(TABLE_EXAMPLE,
                new String[]{COLUMN_SENTENCE_LIST_ID},
                COLUMN_WORD_LIST_ID + " = '" + wordList_id + "'",
                null,
                null,
                null,
                null);

        ArrayList<ExampleSentence> exampleSentencesList = new ArrayList<>();

        // For each sentenceList_id -> Get its english_sentence and translation
        if (example_cursor != null && example_cursor.moveToFirst()) {
            do {
                String sentence_id = example_cursor.getString(0);

                // Cursor to get english_sentences and translation of a word in sentenceList table
                Cursor sentenceCursor = mDatabase.query(TABLE_SENTENCE,
                        new String[]{COLUMN_ID, COLUMN_ENGLISH_SENTENCES, COLUMN_TRANSLATION},
                        COLUMN_ID + " = '" + sentence_id + "'",
                        null,
                        null,
                        null,
                        null);

                // Put example into array list
                if (sentenceCursor != null && sentenceCursor.moveToFirst()) {
                    ExampleSentence exampleSentence = new ExampleSentence();
                    exampleSentence.set_id(Integer.parseInt(sentenceCursor.getString(0)));
                    exampleSentence.setEnglish_sentences(sentenceCursor.getString(1));
                    exampleSentence.setTranslation(StringDecrypter.decrypt(sentenceCursor.getString(2)));
                    exampleSentencesList.add(exampleSentence);

                    sentenceCursor.close();
                }
            } while (example_cursor.moveToNext());

            example_cursor.close();
            return exampleSentencesList;
        } else {
            example_cursor.close();
            return null;
        }
    }

    /**
     * Get a list of suggested words
     *
     * @param displayWord
     * @return
     */
    public ArrayList<String> getSuggestions(String displayWord, int dictionary_type) {
        String table_type = null;

        if (dictionary_type == Constants.ENG_POR) {
            table_type = TABLE_ENGLISH_WORD;
        } else if (dictionary_type == Constants.POR_ENG) {
            table_type = TABLE_PORTUGUESE_WORD;
        }

        if (displayWord.contains("'")) {
            displayWord = displayWord.replace("'", "");
        }

        // Cursor to records in engWordList table
        Cursor cursor = mDatabase.query(table_type,
                new String[]{COLUMN_DISPLAY_WORD},
                COLUMN_DISPLAY_WORD + " LIKE '" + displayWord + "%' ",
                null,
                null,
                null,
                null,
                String.valueOf(40));

        ArrayList<String> suggestionList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                suggestionList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return suggestionList;
    }

    /**
     * Get a random English word
     *
     * @return
     */
    public DictionaryWord getRandomWord() {
        String sql = "SELECT " + COLUMN_ID + ", " + COLUMN_DISPLAY_WORD + ", " + COLUMN_EXPLANATION
                + " FROM " + TABLE_ENGLISH_WORD
                + " WHERE " + COLUMN_ID + " = "
                + " (SELECT " + COLUMN_ID + " FROM " + TABLE_ENGLISH_WORD
                + " WHERE LENGTH('" + COLUMN_DISPLAY_WORD + "') >= 3"
                + " AND " + COLUMN_DISPLAY_WORD + " REGEXP '^[a-zA-Z]*$'"
                + " ORDER BY RANDOM() LIMIT 1);";

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            DictionaryWord randomWord = new DictionaryWord();
            randomWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
            randomWord.setDisplayWord(cursor.getString(1));
            randomWord.setExplanations(cursor.getString(2));

            cursor.close();
            return randomWord;
        } else {
            cursor.close();
            return null;
        }
    }

    /*****************************************************
     * Code to modify and retrieve data in History table *
     *****************************************************/

    /**
     * Put a word into History
     *
     * @param wordList_id
     * @return
     */
    public boolean insertHistory(int wordList_id, String displayWord, String explanations, int dictionary_type) {
        // Return true if a word is not in History -> Add to History
        // otherwise return false
        if (getHistory(wordList_id) == null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_WORD_LIST_ID, wordList_id);
            contentValues.put(COLUMN_DISPLAY_WORD, displayWord);
            contentValues.put(COLUMN_EXPLANATION, explanations);
            contentValues.put(COLUMN_DICTIONARY_TYPE, dictionary_type);
            mDatabase.insert(TABLE_HISTORY, null, contentValues);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get wordList_id from a word in History table
     *
     * @param word_list_id
     * @return
     */
    public BookmarkWord getHistory(int word_list_id) {
        // Cursor to retrieve record in History table
        Cursor cursor = mDatabase.query(TABLE_HISTORY,
                new String[]{COLUMN_WORD_LIST_ID, COLUMN_DISPLAY_WORD, COLUMN_EXPLANATION, COLUMN_DICTIONARY_TYPE},
                COLUMN_WORD_LIST_ID + " = '" + word_list_id + "'",
                null,
                null,
                null,
                null);

        // Return information if exists
        // else return null
        if (cursor != null && cursor.moveToFirst()) {
            BookmarkWord historyWord = new BookmarkWord();
            historyWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
            historyWord.setDisplayWord(cursor.getString(1));
            historyWord.setExplanations(cursor.getString(2));
            historyWord.setDictionary_type(Integer.parseInt(cursor.getString(3)));

            cursor.close();
            return historyWord;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * Get a list of all words in History
     *
     * @return
     */
    public ArrayList<BookmarkWord> getAllHistory() {
        // Cursor to retrieve all records in History table
        Cursor cursor = mDatabase.query(TABLE_HISTORY,
                new String[]{COLUMN_WORD_LIST_ID, COLUMN_DISPLAY_WORD, COLUMN_EXPLANATION, COLUMN_DICTIONARY_TYPE},
                null,
                null,
                null,
                null,
                null);

        ArrayList<BookmarkWord> historyList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookmarkWord historyWord = new BookmarkWord();
                historyWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
                historyWord.setDisplayWord(cursor.getString(1));
                historyWord.setExplanations(cursor.getString(2));
                historyWord.setDictionary_type(Integer.parseInt(cursor.getString(3)));

                historyList.add(historyWord);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return historyList;
    }

    /**
     * Delete all words in History
     */
    public void deleteAllHistory() {
        mDatabase.delete(TABLE_HISTORY, null, null);
    }

    /******************************************************
     * Code to modify and retrieve data in Favorite table *
     ******************************************************/

    /**
     * Put a word into Favorite
     *
     * @param wordList_id
     * @return
     */
    public boolean insertFavorite(int wordList_id, String displayWord, String explanations, int dictionary_type) {
        // Return true if a word is not in Favorite -> add a word to Favorite
        // otherwise return false
        if (getFavorite(wordList_id) == null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_WORD_LIST_ID, wordList_id);
            contentValues.put(COLUMN_DISPLAY_WORD, displayWord);
            contentValues.put(COLUMN_EXPLANATION, explanations);
            contentValues.put(COLUMN_DICTIONARY_TYPE, dictionary_type);
            mDatabase.insert(TABLE_FAVORITE, null, contentValues);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a word from Favorite
     *
     * @param word_list_id
     * @return
     */
    public BookmarkWord getFavorite(int word_list_id) {
        // Cursor to retrieve a record in Favorite table
        Cursor cursor = mDatabase.query(TABLE_FAVORITE,
                new String[]{COLUMN_WORD_LIST_ID, COLUMN_DISPLAY_WORD, COLUMN_EXPLANATION, COLUMN_DICTIONARY_TYPE},
                COLUMN_WORD_LIST_ID + " = '" + word_list_id + "'",
                null,
                null,
                null,
                null);

        // Return favorite if exists
        // else return null
        if (cursor != null && cursor.moveToFirst()) {
            BookmarkWord favoriteWord = new BookmarkWord();
            favoriteWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
            favoriteWord.setDisplayWord(cursor.getString(1));
            favoriteWord.setExplanations(cursor.getString(2));
            favoriteWord.setDictionary_type(Integer.parseInt(cursor.getString(3)));

            cursor.close();
            return favoriteWord;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * Get a list of all words in Favorite
     *
     * @return
     */
    public ArrayList<BookmarkWord> getAllFavorite() {
        // Cursor to retrieve all records in Favorite table
        Cursor cursor = mDatabase.query(TABLE_FAVORITE,
                new String[]{COLUMN_WORD_LIST_ID, COLUMN_DISPLAY_WORD, COLUMN_EXPLANATION, COLUMN_DICTIONARY_TYPE},
                null,
                null,
                null,
                null,
                null);

        ArrayList<BookmarkWord> favoriteList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookmarkWord favoriteWord = new BookmarkWord();
                favoriteWord.setWordList_id(Integer.parseInt(cursor.getString(0)));
                favoriteWord.setDisplayWord(cursor.getString(1));
                favoriteWord.setExplanations(cursor.getString(2));
                favoriteWord.setDictionary_type(Integer.parseInt(cursor.getString(3)));

                favoriteList.add(favoriteWord);
            } while (cursor.moveToNext());

            cursor.close();
            return favoriteList;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * Remove a word from Favorite
     *
     * @param wordList_id
     */
    public void deleteFavorite(int wordList_id) {
        mDatabase.delete(TABLE_FAVORITE,
                COLUMN_WORD_LIST_ID + " = '" + wordList_id + "'",
                null);
    }

    /**
     * Remove all words in Favorite
     */
    public void deleteAllFavorite() {
        mDatabase.delete(TABLE_FAVORITE, null, null);
    }
}
