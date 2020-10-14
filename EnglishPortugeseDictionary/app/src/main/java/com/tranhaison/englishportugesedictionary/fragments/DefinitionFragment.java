package com.tranhaison.englishportugesedictionary.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAdLayout;
import com.tranhaison.englishportugesedictionary.utils.AdsManager;
import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.GoogleTextToSpeech;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.LocalTextToSpeech;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.EnglishDictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.PortugueseDictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.Updatable;

import java.util.ArrayList;

public class DefinitionFragment extends Fragment implements Updatable {

    // Init Views and Layout
    TextView tvExplanation, tvGrammars, tvUSPhonetic, tvUKPhonetic, tvDictionaryWord;
    ImageView ivSpeakerUS, ivSpeakerUK, ivCopyDetail, ivSpeakerDetail;
    CardView cvGrammars, ggUnifiedAdContainerDefinition;
    LinearLayout linearLayoutUSPhonetic, linearLayoutUKPhonetic;
    NativeAdLayout nativeAdLayoutDefinition;

    // Init TTS instances
    LocalTextToSpeech localTextToSpeech;
    GoogleTextToSpeech googleTextToSpeech;

    // Init variables
    private DatabaseHelper databaseHelper;
    private EnglishDictionaryWord englishDictionaryWord;
    private PortugueseDictionaryWord portugueseDictionaryWord;
    private int dictionary_type;

    private String word = "";
    private String explanation = "";
    private String grammar = "";

    public DefinitionFragment(DatabaseHelper databaseHelper, DictionaryWord dictionaryWord, int dictionary_type,
                              LocalTextToSpeech localTextToSpeech, GoogleTextToSpeech googleTextToSpeech) {
        this.databaseHelper = databaseHelper;
        this.dictionary_type = dictionary_type;
        this.localTextToSpeech = localTextToSpeech;
        this.googleTextToSpeech = googleTextToSpeech;

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

        // Create native banner ad
        AdsManager.createFacebookNativeAd(getActivity(), nativeAdLayoutDefinition, ggUnifiedAdContainerDefinition);

        // Get data and set to global variables
        getData();

        // Set visibility and text to Views
        setViewVisibility();
        setTextToView();

        // Handle events
        speakWord();
        copyToClipboard();
    }

    /**
     * Map views from layout file
     * @param view
     */
    private void mapViews(View view) {
        tvExplanation = view.findViewById(R.id.tvExplanation);
        tvGrammars = view.findViewById(R.id.tvGrammars);
        tvUSPhonetic = view.findViewById(R.id.tvUSPhonetic);
        tvUKPhonetic = view.findViewById(R.id.tvUKPhonetic);
        tvDictionaryWord = view.findViewById(R.id.tvDictionaryWord);
        ivSpeakerUS = view.findViewById(R.id.ivSpeakerUS);
        ivSpeakerUK = view.findViewById(R.id.ivSpeakerUK);
        ivCopyDetail = view.findViewById(R.id.ivCopyDetail);
        ivSpeakerDetail = view.findViewById(R.id.ivSpeakerDetail);
        cvGrammars = view.findViewById(R.id.cvGrammars);
        linearLayoutUSPhonetic = view.findViewById(R.id.linearLayoutUSPhonetic);
        linearLayoutUKPhonetic = view.findViewById(R.id.linearLayoutUKPhonetic);
        nativeAdLayoutDefinition = view.findViewById(R.id.nativeAdLayoutDefinition);
        ggUnifiedAdContainerDefinition = view.findViewById(R.id.ggUnifiedAdContainerDefinition);
    }

    /**
     * Get information of a word based on its type
     */
    public void getData() {
        if (dictionary_type == Constants.ENG_POR) {
            word = englishDictionaryWord.getDisplayWord();
            explanation = englishDictionaryWord.getExplanations();
            grammar = englishDictionaryWord.getGrammars();
        } else if (dictionary_type == Constants.POR_ENG) {
            word = portugueseDictionaryWord.getDisplayWord();
            explanation = portugueseDictionaryWord.getExplanations();
            grammar = "";
        }
    }

    /**
     * Set visibility GONE to Views if the text is empty
     */
    private void setViewVisibility() {
        if (grammar.isEmpty()) {
            cvGrammars.setVisibility(View.GONE);
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

        tvDictionaryWord.setText(word);
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
            for (int i=0; i<grammarList.size(); i++) {
                if (i == (grammarList.size() - 1)) {
                    grammar += "* " + grammarList.get(i);
                    break;
                }
                grammar += "* " + grammarList.get(i) + "\n";
            }

            tvGrammars.setText(grammar);
        }
    }

    /**
     * Speak a word when clicking button
     */
    private void speakWord() {
        // Speaker US clicked
        ivSpeakerUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictionary_type == Constants.ENG_POR) {
                    String word = englishDictionaryWord.getDisplayWord();
                    localTextToSpeech.speakEnglish(word, Constants.CODE_US);
                } else {
                    return;
                }
            }
        });

        // Speaker UK clicked
        ivSpeakerUK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictionary_type == Constants.ENG_POR) {
                    String word = englishDictionaryWord.getDisplayWord();
                    localTextToSpeech.speakEnglish(word, Constants.CODE_UK);
                } else {
                    return;
                }
            }
        });

        // Speaker word clicked
        ivSpeakerDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictionary_type == Constants.ENG_POR) {
                    if (NetworkUtil.isNetworkConnected(getContext())) {
                        googleTextToSpeech.play(word, Constants.CODE_ENGLISH);
                    } else {
                        localTextToSpeech.speakEnglish(word, Constants.CODE_US);
                    }
                } else if (dictionary_type == Constants.POR_ENG) {
                    if (NetworkUtil.isNetworkConnected(getContext())) {
                        googleTextToSpeech.play(word, Constants.CODE_PORTUGUESE);
                    } else {
                        localTextToSpeech.speakPortuguese(word);
                    }
                }
            }
        });
    }

    /**
     * Copy a word to clipboard
     */
    private void copyToClipboard() {
        ivCopyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(Constants.CLIPBOARD_LABEL, word);
                clipboard.setPrimaryClip(clip);
            }
        });
    }

    @Override
    public void update(int dictionary_type, int wordList_id) {
        this.dictionary_type = dictionary_type;

        if (this.dictionary_type == Constants.ENG_POR) {
            englishDictionaryWord = databaseHelper.getEngWord(wordList_id);
        } else if (dictionary_type == Constants.POR_ENG) {
            portugueseDictionaryWord = databaseHelper.getPorWord(wordList_id);
        }

        getData();
        setViewVisibility();
        setTextToView();
    }
}