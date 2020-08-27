package com.tranhaison.englishportugesedictionary.fragments.detailactivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.EnglishDictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.PortugueseDictionaryWord;

import java.util.ArrayList;

public class ExplanationFragment extends Fragment {

    // Init View
    TextView tvEnglishExplanation;

    // Init variables
    private int dictionary_type;
    private EnglishDictionaryWord englishDictionaryWord;
    private DatabaseHelper databaseHelper;

    public ExplanationFragment(DatabaseHelper databaseHelper, int dictionary_type, DictionaryWord dictionaryWord) {
        this.dictionary_type = dictionary_type;
        this.databaseHelper = databaseHelper;

        if (dictionary_type == Constants.ENG_POR) {
            englishDictionaryWord = (EnglishDictionaryWord) dictionaryWord;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explanation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map View
        tvEnglishExplanation = view.findViewById(R.id.tvEnglishExplanation);

        // Get english explanation of English word
        getData();

    }

    /**
     * Get english explantion of a word (if exists) and set text to tvEnglishExplanation
     */
    private void getData() {
        if (dictionary_type == Constants.ENG_POR) {
            ArrayList<String> engExplanationList = databaseHelper.getEnglishExplanation(englishDictionaryWord.getWordList_id());

            if (engExplanationList != null) {
                StringBuilder stringBuilder = new StringBuilder();

                for (String explanation : engExplanationList) {
                    stringBuilder.append("* " + explanation + "\n");
                }
                tvEnglishExplanation.setText(stringBuilder.toString());

                return;
            } else {
                tvEnglishExplanation.setText("Not found...");
            }
        }
        tvEnglishExplanation.setText("Not found...");
    }


}