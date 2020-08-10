package com.tranhaison.englishportugesedictionary;

import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;

import java.util.ArrayList;

public class DictionaryDataType {

    private DatabaseHelper databaseHelper;

    public DictionaryDataType(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public ArrayList<DictionaryWord> getData(int dictionary_type) {
        if (dictionary_type == Constants.POR_ENG) {
            return getPorEng();
        } else if (dictionary_type == Constants.ENG_POR) {
            return getEngPor();
        } else {
            return null;
        }
    }

    public ArrayList<DictionaryWord> getPorEng() {
        ArrayList<DictionaryWord> por_eng_list = new ArrayList<>();

        DictionaryWord word1 = new DictionaryWord("disforme", null, null, null, null);
        DictionaryWord word2 = new DictionaryWord("dislexia", null, null, null, null);
        DictionaryWord word3 = new DictionaryWord("disléxico", null, null, null, null);
        DictionaryWord word4 = new DictionaryWord("disparar", null, null, null, null);

        por_eng_list.add(word1);
        por_eng_list.add(word2);
        por_eng_list.add(word3);
        por_eng_list.add(word4);

        return por_eng_list;
    }

    public ArrayList<DictionaryWord> getEngPor() {
        ArrayList<DictionaryWord> eng_por_list = new ArrayList<>();

        DictionaryWord word1 = new DictionaryWord("painful", null, null, null, null);
        DictionaryWord word2 = new DictionaryWord("painkiller", null, null, null, null);
        DictionaryWord word3 = new DictionaryWord("painless", null, null, null, null);
        DictionaryWord word4 = new DictionaryWord("painstaking", null, null, null, null);

        eng_por_list.add(word1);
        eng_por_list.add(word2);
        eng_por_list.add(word3);
        eng_por_list.add(word4);

        return eng_por_list;
    }

    public ArrayList<DictionaryWord> getEngPorSuggestions(String word) {
        ArrayList<DictionaryWord> eng_por_suggestions = databaseHelper.getSuggestions(word);
        return eng_por_suggestions;
    }

}
