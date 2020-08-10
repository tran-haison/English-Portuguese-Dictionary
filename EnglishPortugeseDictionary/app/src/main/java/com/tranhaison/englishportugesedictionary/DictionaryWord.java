package com.tranhaison.englishportugesedictionary;

import java.io.Serializable;

public class DictionaryWord implements Serializable {

    private String word;
    private String definition;
    private String example;
    private String synonym;
    private String antonym;

    public DictionaryWord() {
    }

    public DictionaryWord(String word, String definition, String synonym, String antonym, String example) {
        this.word = word;
        this.definition = definition;
        this.synonym = synonym;
        this.antonym = antonym;
        this.example = example;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getAntonym() {
        return antonym;
    }

    public void setAntonym(String antonym) {
        this.antonym = antonym;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
