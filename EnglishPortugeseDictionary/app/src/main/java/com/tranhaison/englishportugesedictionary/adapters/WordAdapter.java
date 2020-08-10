package com.tranhaison.englishportugesedictionary.adapters;

import com.tranhaison.englishportugesedictionary.DictionaryWord;
import com.tranhaison.englishportugesedictionary.databases.DatabaseAdapter;

import java.util.ArrayList;

public class WordAdapter {

    // Init 2 lists of Favorite and Recent words
    private static ArrayList<DictionaryWord> favoriteWords;
    private static ArrayList<DictionaryWord> recentWords;

    // Init database adapter
    DatabaseAdapter databaseAdapter;

    /**
     * Constructor - pass database adapter to this database adapter
     * @param databaseAdapter
     */
    public WordAdapter(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    /**
     * Get all words from Favorite
     * @return
     */
    public ArrayList<DictionaryWord> getFavoriteWordsList() {
        favoriteWords.clear();
        return favoriteWords = databaseAdapter.getAllWordsFromFavorite();
    }

    /**
     * Get all words from History
     * @return
     */
    public ArrayList<DictionaryWord> getRecentWordsList() {
        recentWords.clear();
        return recentWords = databaseAdapter.getAllWordsFromHistory();
    }

    /**
     * Check if the word is in list of favorite words
     * @param word
     * @return
     */
    public boolean isFavorite(DictionaryWord word) {
        DictionaryWord find_word = databaseAdapter.getWordFromFavorite(word.getWord());
        if (find_word == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if the word is in list of recent words
     * @param word
     * @return
     */
    public boolean isHistory(DictionaryWord word) {
        DictionaryWord find_word = databaseAdapter.getWordFromHistory(word.getWord());
        if (find_word == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Add a word to favorite
     * @param word
     */
    public void addToFavorite(DictionaryWord word) {
        databaseAdapter.addWordToFavorite(word);
    }

    /**
     * Add a word to History
     * @param word
     */
    public void addToHistory(DictionaryWord word) {
        databaseAdapter.addWordToHistory(word);
    }

    /**
     * Remove a word from Favorite
     * @param word
     */
    public void removeFromFavorite(DictionaryWord word) {
        databaseAdapter.removeWordFromFavorite(word);
    }

    /**
     * Remove a word from History
     * @param word
     */
    public void removeFromHistory(DictionaryWord word) {
        databaseAdapter.removeWordFromHistory(word);
    }

    /**
     * Remove all words from list of favorite words
     */
    public void removeAllFromFavorite() {
        databaseAdapter.removeAllWordsFromFavorite();
    }

    /**
     * Remove all words from list of recent words
     */
    public void removeAllFromHistory() {
        databaseAdapter.removeAllWordsFromHistory();
    }

}
