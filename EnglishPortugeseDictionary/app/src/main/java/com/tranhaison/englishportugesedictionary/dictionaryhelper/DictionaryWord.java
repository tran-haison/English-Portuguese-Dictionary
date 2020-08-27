package com.tranhaison.englishportugesedictionary.dictionaryhelper;

import java.io.Serializable;

public class DictionaryWord implements Serializable {

    private int wordList_id;
    private String displayWord;
    private String posTypes;
    private String explanations;

    public DictionaryWord() {
    }

    public DictionaryWord(int wordList_id, String displayWord, String posTypes, String explanations) {
        this.wordList_id = wordList_id;
        this.displayWord = displayWord;
        this.posTypes = posTypes;
        this.explanations = explanations;
    }

    public int getWordList_id() {
        return wordList_id;
    }

    public void setWordList_id(int wordList_id) {
        this.wordList_id = wordList_id;
    }

    public String getDisplayWord() {
        return displayWord;
    }

    public void setDisplayWord(String displayWord) {
        this.displayWord = displayWord;
    }

    public String getPosTypes() {
        return posTypes;
    }

    public void setPosTypes(String posTypes) {
        this.posTypes = posTypes;
    }

    public String getExplanations() {
        return explanations;
    }

    public void setExplanations(String explanations) {
        this.explanations = explanations;
    }
}
