package com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks;

import java.io.Serializable;

public class BookmarkWord implements Serializable {

    private int wordList_id;
    private String displayWord;
    private String explanations;
    private int dictionary_type;

    public BookmarkWord() {
    }

    public BookmarkWord(int wordList_id, String displayWord, String explanations, int dictionary_type) {
        this.wordList_id = wordList_id;
        this.displayWord = displayWord;
        this.explanations = explanations;
        this.dictionary_type = dictionary_type;
    }

    public String getDisplayWord() {
        return displayWord;
    }

    public void setDisplayWord(String displayWord) {
        this.displayWord = displayWord;
    }

    public String getExplanations() {
        return explanations;
    }

    public void setExplanations(String explanations) {
        this.explanations = explanations;
    }

    public int getWordList_id() {
        return wordList_id;
    }

    public void setWordList_id(int wordList_id) {
        this.wordList_id = wordList_id;
    }

    public int getDictionary_type() {
        return dictionary_type;
    }

    public void setDictionary_type(int dictionary_type) {
        this.dictionary_type = dictionary_type;
    }

}
