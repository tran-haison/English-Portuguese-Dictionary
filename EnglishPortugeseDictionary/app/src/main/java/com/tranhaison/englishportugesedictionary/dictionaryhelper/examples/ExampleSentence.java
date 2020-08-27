package com.tranhaison.englishportugesedictionary.dictionaryhelper.examples;

public class ExampleSentence {

    private int _id;
    private String english_sentences;
    private String translation;

    public ExampleSentence() {}

    public ExampleSentence(int _id, String english_sentences, String translation) {
        this._id = _id;
        this.english_sentences = english_sentences;
        this.translation = translation;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getEnglish_sentences() {
        return english_sentences;
    }

    public void setEnglish_sentences(String english_sentences) {
        this.english_sentences = english_sentences;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
