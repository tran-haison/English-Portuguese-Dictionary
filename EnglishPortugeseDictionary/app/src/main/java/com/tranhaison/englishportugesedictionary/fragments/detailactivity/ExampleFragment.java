package com.tranhaison.englishportugesedictionary.fragments.detailactivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.adapters.ExampleAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.EnglishDictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.PortugueseDictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.examples.ExampleSentence;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;
import java.util.Locale;

public class ExampleFragment extends Fragment {

    // Init Views
    LinearLayout linearLayoutExamplePrompt;
    ListView listViewExamples;
    ExampleAdapter exampleAdapter;

    // Init TTS
    TextToSpeech textToSpeechExample;

    // Init variables
    private DatabaseHelper databaseHelper;
    private DictionaryWord dictionaryWord;

    public ExampleFragment(DatabaseHelper databaseHelper, DictionaryWord dictionaryWord) {
        this.databaseHelper = databaseHelper;
        this.dictionaryWord = dictionaryWord;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map Views
        linearLayoutExamplePrompt = view.findViewById(R.id.linearLayoutExamplePrompt);
        listViewExamples = view.findViewById(R.id.listViewExamples);

        // Get example list
        final ArrayList<ExampleSentence> exampleList = getExamples();

        // Set adapter to List View
        if (exampleList != null) {
            exampleAdapter = new ExampleAdapter(getActivity(), exampleList);
            listViewExamples.setAdapter(exampleAdapter);

            // Init TTS instance
            initTextToSpeech();

            // Speaker clicked event
            exampleAdapter.setOnItemSpeakerClick(new ListItemListener() {
                @Override
                public void onItemClick(int position) {
                    String english_sentences = exampleList.get(position).getEnglish_sentences();
                    textToSpeechExample.speak(english_sentences, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        } else {
            listViewExamples.setVisibility(View.GONE);
            linearLayoutExamplePrompt.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Get a list of examples of a word
     * @return
     */
    private ArrayList<ExampleSentence> getExamples() {
        ArrayList<ExampleSentence> exampleList = databaseHelper.getExamples(dictionaryWord.getWordList_id());
        return exampleList;
    }

    /**
     * Init TTS instance and set language to it
     */
    private void initTextToSpeech() {
        textToSpeechExample = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeechExample.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getActivity(), "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Initialize failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}