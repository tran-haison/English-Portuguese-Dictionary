package com.tranhaison.englishportugesedictionary.utils.texttospeech;

import android.content.Context;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.Constants;

import java.util.Locale;

public class LocalTextToSpeech {

    private TextToSpeech textToSpeechUS, textToSpeechUK, textToSpeechPortuguese;
    private Context context;

    public LocalTextToSpeech(Context context) {
        this.context = context;
    }

    /**
     * Initialize text to speech instance
     */
    public void initialize() {

        // US text to speech
        textToSpeechUS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeechUS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, context.getResources().getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.initialize_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // UK text to speech
        textToSpeechUK = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeechUK.setLanguage(Locale.UK);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, context.getResources().getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.initialize_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Portuguese text to speech
        textToSpeechPortuguese = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeechPortuguese.setLanguage(new Locale("pt", "POR"));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, context.getResources().getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.initialize_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void shutdown() {
        // Shutdown tts instances
        if (textToSpeechPortuguese != null) {
            textToSpeechPortuguese.stop();
            textToSpeechPortuguese.shutdown();
        }

        if (textToSpeechUS != null) {
            textToSpeechUS.stop();
            textToSpeechUS.shutdown();
        }

        if (textToSpeechUK != null) {
            textToSpeechUK.stop();
            textToSpeechUK.shutdown();
        }
    }

    public void speakEnglish(String text, String accent) {
        if (accent == Constants.CODE_US) {
            textToSpeechUS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else if (accent == Constants.CODE_UK) {
            textToSpeechUK.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void speakPortuguese(String text) {
        textToSpeechPortuguese.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
