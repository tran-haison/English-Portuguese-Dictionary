package com.tranhaison.englishportugesedictionary;

import java.io.Serializable;

public class DictionaryWord implements Serializable {

    private String word;
    private String content;

    public DictionaryWord() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
