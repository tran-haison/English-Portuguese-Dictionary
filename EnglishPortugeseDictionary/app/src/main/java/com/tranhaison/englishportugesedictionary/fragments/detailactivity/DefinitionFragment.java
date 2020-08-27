package com.tranhaison.englishportugesedictionary.fragments.detailactivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.activities.DetailActivity;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.EnglishDictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.PortugueseDictionaryWord;

import java.util.ArrayList;
import java.util.Locale;

public class DefinitionFragment extends Fragment {

    // Init Views and Layout
    TextView tvExplanation, tvGrammars, tvGrammarsPrompt, tvUSPhonetic, tvUKPhonetic;
    ImageView ivSpeakerUS, ivSpeakerUK;
    LinearLayout linearLayoutUSPhonetic, linearLayoutUKPhonetic;

    // Init TTS instances
    TextToSpeech textToSpeechUS, textToSpeechUK;

    // Init variables
    private DatabaseHelper databaseHelper;
    private EnglishDictionaryWord englishDictionaryWord;
    private PortugueseDictionaryWord portugueseDictionaryWord;
    private int dictionary_type;

    private String explanation = "";
    private String grammar = "";

    public DefinitionFragment(DatabaseHelper databaseHelper, DictionaryWord dictionaryWord, int dictionary_type) {
        this.databaseHelper = databaseHelper;
        this.dictionary_type = dictionary_type;

        if (dictionary_type == Constants.ENG_POR) {
            englishDictionaryWord = (EnglishDictionaryWord) dictionaryWord;
        } else if (dictionary_type == Constants.POR_ENG) {
            portugueseDictionaryWord = (PortugueseDictionaryWord) dictionaryWord;
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
        return inflater.inflate(R.layout.fragment_definition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map Views and Layouts
        mapViews(view);

        // Get data and set to global variables
        getData();

        // Set visibility and text to Views
        setViewVisibility();
        setTextToView();

        // Handle events
        initTextToSpeech();
        speakWord();
    }

    /**
     * Map views from layout file
     * @param view
     */
    private void mapViews(View view) {
        tvExplanation = view.findViewById(R.id.tvExplanation);
        tvGrammars = view.findViewById(R.id.tvGrammars);
        tvGrammarsPrompt = view.findViewById(R.id.tvGrammarsPrompt);
        tvUSPhonetic = view.findViewById(R.id.tvUSPhonetic);
        tvUKPhonetic = view.findViewById(R.id.tvUKPhonetic);
        ivSpeakerUS = view.findViewById(R.id.ivSpeakerUS);
        ivSpeakerUK = view.findViewById(R.id.ivSpeakerUK);
        linearLayoutUSPhonetic = view.findViewById(R.id.linearLayoutUSPhonetic);
        linearLayoutUKPhonetic = view.findViewById(R.id.linearLayoutUKPhonetic);
    }

    /**
     * Get information of a word based on its type
     */
    public void getData() {
        if (dictionary_type == Constants.ENG_POR) {
            explanation = englishDictionaryWord.getExplanations();
            grammar = englishDictionaryWord.getGrammars();
        } else if (dictionary_type == Constants.POR_ENG) {
            explanation = portugueseDictionaryWord.getExplanations();
        }
    }

    /**
     * Set visibility GONE to Views if the text is empty
     */
    private void setViewVisibility() {
        if (grammar.isEmpty()) {
            tvGrammars.setVisibility(View.GONE);
            tvGrammarsPrompt.setVisibility(View.GONE);
        }

        if (dictionary_type == Constants.POR_ENG) {
            linearLayoutUSPhonetic.setVisibility(View.GONE);
            linearLayoutUKPhonetic.setVisibility(View.GONE);
        } else if (dictionary_type == Constants.ENG_POR) {
            if (englishDictionaryWord.getUS_phonetic().isEmpty()) {
                linearLayoutUSPhonetic.setVisibility(View.GONE);
            }

            if (englishDictionaryWord.getUK_phonetic().isEmpty()) {
                linearLayoutUKPhonetic.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Set texts to their corresponded Views if they are not empty
     */
    public void setTextToView() {
        ArrayList<String> definitionList;

        if (dictionary_type == Constants.ENG_POR) {
            definitionList = databaseHelper.getDefinitionForWord(englishDictionaryWord.getWordList_id(), dictionary_type);
        } else {
            definitionList = databaseHelper.getDefinitionForWord(portugueseDictionaryWord.getWordList_id(), dictionary_type);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i < definitionList.size(); i++) {
            if (i == (definitionList.size() - 1)) {
                stringBuilder.append("* " + definitionList.get(i));
                break;
            }
            stringBuilder.append("* " + definitionList.get(i) + "\n");
        }
        
        tvExplanation.setText(stringBuilder.toString());

        // Set US and UK phonetic to their text view
        if (dictionary_type == Constants.ENG_POR) {
            tvUSPhonetic.setText("[" + englishDictionaryWord.getUS_phonetic() + "]");
            tvUKPhonetic.setText("[" + englishDictionaryWord.getUK_phonetic() + "]");
        }

        // Set grammars to text view if it is not empty
        if (!grammar.isEmpty()) {
            // Set grammar to initial value because of database
            grammar = "";

            // Get list of all types of that word
            ArrayList<String> grammarList = databaseHelper.getGrammars(englishDictionaryWord.getWordList_id());
            for (String grammar_word : grammarList) {
                grammar += "* " + grammar_word + "\n";
            }

            tvGrammars.setText(grammar);
        }
    }

    /**
     * Set the language for instances
     */
    private void initTextToSpeech() {
        if (linearLayoutUSPhonetic.getVisibility() == View.VISIBLE) {
            textToSpeechUS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i == TextToSpeech.SUCCESS) {
                        int result = textToSpeechUS.setLanguage(Locale.US);

                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(getActivity(), "Language not supported", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Initialize failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (linearLayoutUKPhonetic.getVisibility() == View.VISIBLE) {
            textToSpeechUK = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i == TextToSpeech.SUCCESS) {
                        int result = textToSpeechUK.setLanguage(Locale.UK);

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

    /**
     * Speak a word when clicking button
     */
    private void speakWord() {
        ivSpeakerUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictionary_type == Constants.ENG_POR) {
                    String word = englishDictionaryWord.getDisplayWord();
                    textToSpeechUS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    return;
                }
            }
        });

        ivSpeakerUK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictionary_type == Constants.ENG_POR) {
                    String word = englishDictionaryWord.getDisplayWord();
                    textToSpeechUK.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    return;
                }
            }
        });
    }

}