package com.tranhaison.englishportugesedictionary.fragments.mainactivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.DictionaryState;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;

import java.util.Locale;

public class MainFragment extends Fragment {

    // Init views
    TextView tvWordOfTheDay, tvWordOfTheDayExplanation, tvOutputText;
    ImageView ivRefresh, ivSpeakerWordOfTheDay, ivCopy, ivSwap, ivFlagInput, ivFlagOutput;
    EditText etInputText, etSearchOnline;
    Button btnTranslate, btnSearchOnline;

    // Text to speech instance
    TextToSpeech textToSpeech;

    // Init global variables
    private DictionaryWord dictionaryWord;
    private DatabaseHelper databaseHelper;
    private int dictionary_type = Constants.ENG_POR;

    // Init fragment listener interface
    private FragmentListener fragmentListener;

    public MainFragment(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map views
        mapViews(view);

        // Get word of the day from MainActivity
        getWordOfTheDay();

        // Handle button clicked event
        refreshWordOfTheDay();
        copyWordToClipboard();
        searchOnline();

        initTextToSpeech();
        speakWord();


        swapTextTranslationType();
        getSpeechText();

        // Translate button clicked
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etInputText.getText().toString();
                if (!text.isEmpty()) {
                    downloadModel(text);
                }
            }
        });

    }

    /**
     * Map views from layout file
     */
    private void mapViews(@NonNull View view) {
        tvWordOfTheDay = view.findViewById(R.id.tvWordOfTheDay);
        tvWordOfTheDayExplanation = view.findViewById(R.id.tvWordOfTheDayExplanation);
        tvOutputText = view.findViewById(R.id.tvOutputText);
        ivRefresh = view.findViewById(R.id.ivRefresh);
        ivSpeakerWordOfTheDay = view.findViewById(R.id.ivSpeakerWordOfTheDay);
        ivCopy = view.findViewById(R.id.ivCopy);
        ivFlagInput = view.findViewById(R.id.ivFlagInput);
        ivFlagOutput = view.findViewById(R.id.ivFlagOutput);
        etInputText = view.findViewById(R.id.etInputText);
        etSearchOnline = view.findViewById(R.id.etSearchOnline);
        ivSwap = view.findViewById(R.id.ivSwap);
        btnTranslate = view.findViewById(R.id.btnTranslate);
        btnSearchOnline = view.findViewById(R.id.btnSearchOnline);
    }

    /**
     * Get a random English word each day and set to text views
     * if a new word is passed -> save to shared preferences
     * else load the old one
     */
    private void getWordOfTheDay() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            dictionaryWord = (DictionaryWord) bundle.getSerializable("random_word");

            // Get word from MainActivity
            String displayWord = dictionaryWord.getDisplayWord();
            String explanation = dictionaryWord.getExplanations();

            //dictionaryWord.setDisplayWord(displayWord);
            //dictionaryWord.setExplanations(explanation);

            tvWordOfTheDay.setText(displayWord);
            tvWordOfTheDayExplanation.setText(explanation);

            // Save the new word to shared preferences
            DictionaryState.saveState(getActivity(), Constants.WORD_OF_THE_DAY, displayWord);
            DictionaryState.saveState(getActivity(), Constants.WORD_OF_THE_DAY_EXPLANATION, explanation);
        } else {
            // Load the word from shared preferences and set to text view
            String displayWord = DictionaryState.getState(getActivity(), Constants.WORD_OF_THE_DAY);
            String explanation = DictionaryState.getState(getActivity(), Constants.WORD_OF_THE_DAY_EXPLANATION);

            dictionaryWord = new DictionaryWord();
            dictionaryWord.setDisplayWord(displayWord);
            dictionaryWord.setExplanations(explanation);

            if (displayWord != null && explanation != null) {
                tvWordOfTheDay.setText(displayWord);
                tvWordOfTheDayExplanation.setText(explanation);
            }
        }
    }

    /**
     * Button refresh is clicked -> Get a new random word
     */
    private void refreshWordOfTheDay() {
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotate_anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
                ivRefresh.setAnimation(rotate_anim);

                getRandomWord();
            }
        });
    }

    /**
     * Get a new random word
     * Set word and its explanation to text view
     * And save to shared preferences for future used
     */
    private void getRandomWord() {
        dictionaryWord = databaseHelper.getRandomWord();

        String displayWord = dictionaryWord.getDisplayWord();
        String explanation = dictionaryWord.getExplanations();
        tvWordOfTheDay.setText(displayWord);
        tvWordOfTheDayExplanation.setText(explanation);

        DictionaryState.saveState(getActivity(), Constants.WORD_OF_THE_DAY, displayWord);
        DictionaryState.saveState(getActivity(), Constants.WORD_OF_THE_DAY_EXPLANATION, explanation);
    }

    /**
     * Copy a word to clipboard
     */
    private void copyWordToClipboard() {
        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayWord = tvWordOfTheDay.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(Constants.CLIPBOARD_LABEL, displayWord);
                clipboard.setPrimaryClip(clip);
            }
        });
    }

    /**
     * Initialize text to speech instance
     */
    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getActivity(), "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Initialize failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Speak a word when pressing button
     */
    private void speakWord() {
        ivSpeakerWordOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = dictionaryWord.getDisplayWord();
                textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    /**
     * Search a word online
     */
    private void searchOnline() {
        btnSearchOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchWord = etSearchOnline.getText().toString();

                // If user does not type a word -> display a prompt
                // else pass searching word to OnlineSearchingActivity
                if (searchWord.isEmpty()) {
                    Toast.makeText(getActivity(), "Please type a word", Toast.LENGTH_SHORT).show();
                } else {
                    if (fragmentListener != null) {
                        fragmentListener.onItemClick(searchWord);
                    }
                }
            }
        });
    }

    /**
     * Swap text input and output type between English and Portuguese
     */
    private void swapTextTranslationType() {
        ivSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotate_anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
                ivSwap.setAnimation(rotate_anim);

                if (dictionary_type == Constants.ENG_POR) {
                    dictionary_type = Constants.POR_ENG;

                    changeImageViewAnimation(ivFlagInput, R.drawable.img_portuguese_flag, R.anim.slide_right_animation);
                    changeImageViewAnimation(ivFlagOutput, R.drawable.img_english_flag, R.anim.slide_left_animation);
                } else {
                    dictionary_type = Constants.ENG_POR;

                    changeImageViewAnimation(ivFlagInput, R.drawable.img_english_flag, R.anim.slide_right_animation);
                    changeImageViewAnimation(ivFlagOutput, R.drawable.img_portuguese_flag, R.anim.slide_left_animation);
                }
            }
        });
    }

    /**
     * Set animation to image view when swapping between text input and output
     * @param imageView
     * @param image_resource
     * @param anim
     */
    public void changeImageViewAnimation(final ImageView imageView, final int image_resource, int anim) {
        final Animation anim_out = AnimationUtils.loadAnimation(getContext(), anim);
        //final Animation anim_in  = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_animation);

        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                imageView.setImageResource(image_resource);
                /*anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                imageView.startAnimation(anim_in);*/
            }
        });
        imageView.startAnimation(anim_out);
    }

    /**
     * Download translator model if needed and translate text depending on input text
     * @param text
     */
    public void downloadModel(final String text) {
        // Create an English-Portuguese translator:
        TranslatorOptions options;
        if (dictionary_type == Constants.POR_ENG) {
            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.PORTUGUESE)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
        } else {
            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.PORTUGUESE)
                    .build();
        }

        final Translator translator = Translation.getClient(options);

        // Downloaded model conditions (required Wifi because of its size)
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        // Download model if needed
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                // Model downloaded successfully. Okay to start translating.
                                translateText(text, translator);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Model could not be downloaded", Toast.LENGTH_SHORT).show();
                            }
                        });

    }

    /**
     * Translate text
     * @param text
     * @param translator
     */
    private void translateText(final String text, final Translator translator) {
        translator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                // Translation successful.
                                tvOutputText.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error.
                                Toast.makeText(getActivity(), "Error translation", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    /**
     * Get speech text from Main Activity and translate it
     */
    private void getSpeechText() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String text = bundle.getString(Constants.TEXT_TRANSLATION);
            if (!text.isEmpty()) {
                etInputText.setText(text);
                downloadModel(text);
            }
        }
    }

    /**
     * Constructor for interface
     *
     * @param fragmentListener
     */
    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    @Override
    public void onDestroy() {
        textToSpeech.stop();
        textToSpeech.shutdown();
        super.onDestroy();
    }
}