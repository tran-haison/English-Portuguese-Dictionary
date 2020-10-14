package com.tranhaison.englishportugesedictionary.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.GoogleTextToSpeech;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.LocalTextToSpeech;
import com.tranhaison.englishportugesedictionary.activities.TextTranslationActivity;
import com.tranhaison.englishportugesedictionary.adapters.detailactivity.fragment.ExplanationAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.EnglishDictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;
import com.tranhaison.englishportugesedictionary.interfaces.Updatable;

import java.util.ArrayList;

public class ExplanationFragment extends Fragment implements Updatable {

    // Init View
    ListView listViewExplanation;
    LinearLayout linearLayoutExplanation;

    LocalTextToSpeech localTextToSpeech;
    GoogleTextToSpeech googleTextToSpeech;

    // Init variables
    private int dictionary_type;
    private EnglishDictionaryWord englishDictionaryWord;
    private DatabaseHelper databaseHelper;
    private ExplanationAdapter explanationAdapter;
    private ArrayList<String> explanationList;

    public ExplanationFragment(DatabaseHelper databaseHelper, int dictionary_type, DictionaryWord dictionaryWord,
                               LocalTextToSpeech localTextToSpeech, GoogleTextToSpeech googleTextToSpeech) {
        this.dictionary_type = dictionary_type;
        this.databaseHelper = databaseHelper;
        this.localTextToSpeech = localTextToSpeech;
        this.googleTextToSpeech = googleTextToSpeech;

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
        listViewExplanation = view.findViewById(R.id.listViewExplanation);
        linearLayoutExplanation = view.findViewById(R.id.linearLayoutExplanation);

        // Get explanation (if exists)
        getData();
        setViewVisibility();
        setItemClicked();
    }

    /**
     * Get english explanation of a word (if exists) and set text to tvEnglishExplanation
     */
    private void getData() {
        if (dictionary_type == Constants.ENG_POR) {
            explanationList = databaseHelper.getEnglishExplanation(englishDictionaryWord.getWordList_id());

            if (explanationList != null) {
                explanationAdapter = new ExplanationAdapter(getActivity(), explanationList);
                listViewExplanation.setAdapter(explanationAdapter);

                speakExplanationSentence();
            }
        } else {
            explanationList = null;
        }
    }

    /**
     * Set view visibility if exists
     */
    private void setViewVisibility() {
        if (explanationList != null && explanationList.size() > 0) {
            linearLayoutExplanation.setVisibility(View.GONE);
            listViewExplanation.setVisibility(View.VISIBLE);
        } else {
            linearLayoutExplanation.setVisibility(View.VISIBLE);
            listViewExplanation.setVisibility(View.GONE);
        }
    }

    /**
     * List view item clicked handler
     */
    private void setItemClicked() {
        try {
            explanationAdapter.setOnItemClicked(new ListItemListener() {
                @Override
                public void onItemClick(int position) {
                    String explanation = explanationList.get(position);

                    Intent intent = new Intent(getActivity(), TextTranslationActivity.class);
                    intent.putExtra(Constants.TEXT_TRANSLATION, explanation);
                    startActivity(intent);
                }
            });
        } catch (Exception  e) {}
    }

    /**
     * Text to speech for explanation sentences
     */
    private void speakExplanationSentence() {
        explanationAdapter.setOnItemSpeakerClicked(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                String explanation = explanationList.get(position);
                if (NetworkUtil.isNetworkConnected(getContext())) {
                    googleTextToSpeech.play(explanation, Constants.CODE_ENGLISH);
                } else {
                    localTextToSpeech.speakEnglish(explanation, Constants.CODE_US);
                }
            }
        });
    }

    @Override
    public void update(int dictionary_type, int wordList_id) {
        this.dictionary_type = dictionary_type;

        if (this.dictionary_type == Constants.ENG_POR) {
            englishDictionaryWord = databaseHelper.getEngWord(wordList_id);
        }

        getData();
        setViewVisibility();
        setItemClicked();
    }
}