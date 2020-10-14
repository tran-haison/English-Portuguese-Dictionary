package com.tranhaison.englishportugesedictionary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.GoogleTextToSpeech;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.LocalTextToSpeech;
import com.tranhaison.englishportugesedictionary.adapters.detailactivity.fragment.ExampleAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.examples.ExampleSentence;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;
import com.tranhaison.englishportugesedictionary.interfaces.Updatable;

import java.util.ArrayList;

public class ExampleFragment extends Fragment implements Updatable {

    // Init View and Layout
    LinearLayout linearLayoutExamplePrompt;
    ListView listViewExamples;

    // Init adapter and array for list view
    private ExampleAdapter exampleAdapter;
    private ArrayList<ExampleSentence> exampleList;

    // Init variables
    private DatabaseHelper databaseHelper;
    private int wordList_id;
    private int dictionary_type;
    private String current_word;

    LocalTextToSpeech localTextToSpeech;
    GoogleTextToSpeech googleTextToSpeech;

    public ExampleFragment(DatabaseHelper databaseHelper, int wordList_id,
                           String current_word, int dictionary_type,
                           LocalTextToSpeech localTextToSpeech, GoogleTextToSpeech googleTextToSpeech) {
        this.databaseHelper = databaseHelper;
        this.wordList_id = wordList_id;
        this.current_word = current_word;
        this.dictionary_type = dictionary_type;
        this.localTextToSpeech = localTextToSpeech;
        this.googleTextToSpeech = googleTextToSpeech;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_example, container, false);

        // Map Views
        linearLayoutExamplePrompt = view.findViewById(R.id.linearLayoutExamplePrompt);
        listViewExamples = view.findViewById(R.id.listViewExamples);

        // Init array list
        exampleList = new ArrayList<>();

        // Get example list
        getExamples();

        // Set example to list view
        setExampleToListView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Get a list of examples of a word
     *
     * @return
     */
    private void getExamples() {
        if (exampleList != null) {
            exampleList.clear();
        }

        exampleList = databaseHelper.getExamples(wordList_id);
    }

    /**
     * Set example to list view if exists
     * otherwise show a prompt
     */
    private void setExampleToListView() {
        if (exampleList != null) {
            // Set adapter to List View
            exampleAdapter = new ExampleAdapter(getActivity(), exampleList, current_word);
            exampleAdapter.setVariables(dictionary_type, databaseHelper, wordList_id);
            try {
                listViewExamples.setAdapter(exampleAdapter);
            } catch (Exception e) {}

            // Speaker clicked
            speakItem();
        } else {
            try {
                listViewExamples.setVisibility(View.GONE);
                linearLayoutExamplePrompt.setVisibility(View.VISIBLE);
            } catch (Exception e) {}
        }
    }

    /**
     * Text to speech an item in list view
     */
    private void speakItem() {
        // Speaker clicked event
        exampleAdapter.setOnItemSpeakerClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                String english_sentences = exampleList.get(position).getEnglish_sentences();

                if (NetworkUtil.isNetworkConnected(getContext())) {
                    googleTextToSpeech.play(english_sentences, Constants.CODE_ENGLISH);
                } else {
                    localTextToSpeech.speakEnglish(english_sentences, Constants.CODE_US);
                }
            }
        });
    }

    public void updateCurrentWord(String current_word) {
        this.current_word = current_word;
    }

    @Override
    public void update(int dictionary_type, int wordList_id) {
        this.wordList_id = wordList_id;
        this.dictionary_type = dictionary_type;

        getExamples();
        setExampleToListView();
    }
}