package com.tranhaison.englishportugesedictionary.dictionaryhelper;

public class EnglishDictionaryWord extends DictionaryWord {

    private String US_phonetic;
    private String UK_phonetic;
    private String engPosTypes;
    private String engExplanations;
    private String grammarTypes;
    private String grammars;

    public EnglishDictionaryWord() {
    }

    public EnglishDictionaryWord(int wordList_id, String displayWord,
                                 String posTypes, String explanations,
                                 String US_phonetic, String UK_phonetic,
                                 String engPosTypes, String engExplanations,
                                 String grammarTypes, String grammars) {
        super(wordList_id, displayWord, posTypes, explanations);
        this.US_phonetic = US_phonetic;
        this.UK_phonetic = UK_phonetic;
        this.engPosTypes = engPosTypes;
        this.engExplanations = engExplanations;
        this.grammarTypes = grammarTypes;
        this.grammars = grammars;
    }

    public String getUS_phonetic() {
        return US_phonetic;
    }

    public void setUS_phonetic(String US_phonetic) {
        this.US_phonetic = US_phonetic;
    }

    public String getUK_phonetic() {
        return UK_phonetic;
    }

    public void setUK_phonetic(String UK_phonetic) {
        this.UK_phonetic = UK_phonetic;
    }

    public String getEngPosTypes() {
        return engPosTypes;
    }

    public void setEngPosTypes(String engPosTypes) {
        this.engPosTypes = engPosTypes;
    }

    public String getEngExplanations() {
        return engExplanations;
    }

    public void setEngExplanations(String engExplanations) {
        this.engExplanations = engExplanations;
    }

    public String getGrammarTypes() {
        return grammarTypes;
    }

    public void setGrammarTypes(String grammarTypes) {
        this.grammarTypes = grammarTypes;
    }

    public String getGrammars() {
        return grammars;
    }

    public void setGrammars(String grammars) {
        this.grammars = grammars;
    }
}
