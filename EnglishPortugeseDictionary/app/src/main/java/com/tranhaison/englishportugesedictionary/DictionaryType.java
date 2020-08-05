package com.tranhaison.englishportugesedictionary;

import java.util.ArrayList;

public class DictionaryType {

    public static ArrayList<String> getData(int id) {
        if (id == Constants.POR_ENG) {
            return getPorEng();
        } else if (id == Constants.ENG_POR) {
            return getEngPor();
        } else {
            return null;
        }
    }

    public static ArrayList<String> getPorEng() {
        ArrayList<String> por_eng_list = new ArrayList<>();
        por_eng_list.add("disforme");
        por_eng_list.add("dislexia");
        por_eng_list.add("disléxico");
        por_eng_list.add("disparar");
        por_eng_list.add("disparate");
        por_eng_list.add("disparo");

        return por_eng_list;
    }

    public static ArrayList<String> getEngPor() {
        ArrayList<String> eng_por_list = new ArrayList<>();
        eng_por_list.add("painful");
        eng_por_list.add("pain in the abdomen");
        eng_por_list.add("painkiller");
        eng_por_list.add("painless");
        eng_por_list.add("painstaking");
        eng_por_list.add("paint");

        return eng_por_list;
    }
}
