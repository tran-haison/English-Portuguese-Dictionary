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
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.adapters.SynonymAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;
import java.util.Locale;

public class SynonymFragment extends Fragment {

    // Init View
    LinearLayout linearLayoutSynonymPrompt;
    ListView listViewSynonym;

    // Init text to speech
    TextToSpeech textToSpeech;

    // Init global variables
    private DictionaryWord dictionaryWord;
    private DatabaseHelper databaseHelper;
    private int dictionary_type;

    // Init adapter and array
    private SynonymAdapter synonymAdapter;
    private ArrayList<DictionaryWord> relatedWordList;

    // Init fragment listener
    private FragmentListener fragmentListener;

    public SynonymFragment(DatabaseHelper databaseHelper, DictionaryWord dictionaryWord, int dictionary_type) {
        this.databaseHelper = databaseHelper;
        this.dictionaryWord = dictionaryWord;
        this.dictionary_type = dictionary_type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_synonym, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map View
        linearLayoutSynonymPrompt = view.findViewById(R.id.linearLayoutSynonymPrompt);
        listViewSynonym = view.findViewById(R.id.listViewSynonym);

        // Get list of related words
        getRelatedWordList();

        // If a word has other related words -> Show in list view and handle event for each related word
        // else display prompt
        if (hasRelatedWord()) {
            // Set adapter to list view
            synonymAdapter = new SynonymAdapter(getActivity(), relatedWordList);
            listViewSynonym.setAdapter(synonymAdapter);

            // Init instance
            initTextToSpeech();

            // Override the event handler from adapter
            synonymAdapter.setOnItemClick(new ListItemListener() {
                @Override
                public void onItemClick(int position) {
                    if (fragmentListener != null) {
                        fragmentListener.onItemClick(relatedWordList.get(position).getDisplayWord());
                    }
                }
            });

            // Override the event handler from adapter
            synonymAdapter.setOnItemSpeakerClick(new ListItemListener() {
                @Override
                public void onItemClick(int position) {
                    String displayWord = relatedWordList.get(position).getDisplayWord();
                    textToSpeech.speak(displayWord, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        } else {
            linearLayoutSynonymPrompt.setVisibility(View.VISIBLE);
            listViewSynonym.setVisibility(View.GONE);
        }
    }

    /**
     * Get list of related words depending on dictionary_type
     * @return
     */
    private void getRelatedWordList() {
        if (dictionary_type == Constants.ENG_POR) {
            relatedWordList = databaseHelper.getPorRelatedWord(dictionaryWord.getWordList_id());
        } else if (dictionary_type == Constants.POR_ENG) {
            relatedWordList = databaseHelper.getEngRelatedWord(dictionaryWord.getWordList_id());
        }
    }

    /**
     * Check to see if a word has its related words or not
     * @return
     */
    private boolean hasRelatedWord() {
        if (relatedWordList != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pass interface instance as parameter
     * @param fragmentListener
     */
    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    /**
     * Init TTS instance
     */
    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {

                    int result;
                    if (dictionary_type == Constants.POR_ENG) {
                        result = textToSpeech.setLanguage(Locale.ENGLISH);
                    } else {
                        result = textToSpeech.setLanguage(new Locale("pt", "POR"));
                    }

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