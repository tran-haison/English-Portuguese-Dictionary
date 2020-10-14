package com.tranhaison.englishportugesedictionary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.activities.TextTranslationActivity;

public class Utils {

    public static void openKeyboard(View view, Context context) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Set color to a particular word in sentence
     * then return the new sentence with the coloring of the word
     * @param word
     * @param sentence
     * @return
     */
    public static String getNewColoredSentence(String word, String sentence) {
        // Get the lower case string for finding index of word
        String lowerDisplayWord = word.toLowerCase().trim();
        String lowerEnglishSentence = sentence.toLowerCase().trim();
        int index = lowerEnglishSentence.indexOf(lowerDisplayWord);

        String oldWord = null;
        boolean isNextCharacter = false;
        boolean isPreviousCharacter = false;

        if (index >= 0) {
            oldWord = sentence.substring(index, index + word.length());
            isNextCharacter = Character.isLetter(sentence.charAt(index + word.trim().length()));

            if (index != 0) {
                isPreviousCharacter = Character.isLetter(sentence.charAt(index - 1));
            }
        }

        if (index == -1) {
            return sentence;
        } else if (index == 0) {
            if (!isNextCharacter) {
                sentence = sentence.replaceAll("(?i)" + word, setWordColor(oldWord));
            }
        } else if ((index + word.trim().length()) == sentence.length()){
            if (!isPreviousCharacter) {
                sentence = sentence.replaceAll("(?i)" + word, setWordColor(oldWord));
            }
        } else {
            if (!isPreviousCharacter && !isNextCharacter) {
                sentence = sentence.replaceAll("(?i)" + word, setWordColor(oldWord));
            }
        }

        return sentence;
    }

    /**
     * Set color to word
     * color code = #960510 (red)
     * @param word
     * @return
     */
    public static String setWordColor(String word) {
        return "<font color='#960510'>" + word + "</font>";
    }
}
