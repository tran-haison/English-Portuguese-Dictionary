package com.tranhaison.englishportugesedictionary;

public class Constants {

    // Key to save latest state
    public static final String DICTIONARY_TYPE = "dictionary_type";
    public static final String LAST_TIME_OPEN = "last_time_open";

    // Key to pass data from Activity
    public static final String WORD_OF_THE_DAY = "word_of_the_day";
    public static final String WORD_OF_THE_DAY_EXPLANATION = "word_of_the_day_explanation";
    public static final String SUGGESTION_LIST = "suggestion_list";
    public static final String WORD_LIST_ID = "wordList_id";
    public static final String SEARCH_WORD = "search_word";
    public static final String TEXT_TRANSLATION = "text_translation";

    // Label to copy a word to clipboard
    public static final String CLIPBOARD_LABEL = "clipboard_label";

    // Dictionary type to get data sources
    public static final int ENG_POR = 1;
    public static final int POR_ENG = 2;

    // Request code for speech recognizer
    public static final int REQUEST_SPEECH_RECOGNIZER = 1;

    // Splash screen timer
    public static final int SPLASH_SCREEN_TIMER = 4000;

    // Scale to calculate offset X and Y for menu navigation and main layout animation in MainActivity
    public static final float END_SCALE = 0.7f;

    // Convert from time in millis to day
    public static final int MILLIS_TO_DAY = 86400000;
}
