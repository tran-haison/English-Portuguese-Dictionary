package com.tranhaison.englishportugesedictionary.utils;

public class Constants {

    public static final String APP_ID = "com.tranhaison.englishportugesedictionary";

    // Key to save latest state
    public static final String DICTIONARY_TYPE = "dictionary_type";
    public static final String LAST_TIME_OPEN = "last_time_open";
    public static final String MODEL_DOWNLOADED = "model_downloaded";

    // Key to pass data from Activity to another Activity
    public static final String WORD_LIST_ID = "wordList_id";
    public static final String SEARCH_WORD = "search_word";
    public static final String TEXT_TRANSLATION = "text_translation";

    // Label to copy text to clipboard
    public static final String CLIPBOARD_LABEL = "clipboard_label";

    // Dictionary type
    public static final int ENG_POR = 1;
    public static final int POR_ENG = 2;

    // Country code
    public static final String CODE_US = "us";
    public static final String CODE_UK = "uk";
    public static final String CODE_ENGLISH = "en";
    public static final String CODE_PORTUGUESE = "pt";

    // Request code for calling intent result
    public static final int REQUEST_SPEECH_RECOGNIZER = 1;
    public static final int REQUEST_UPDATE_FAVORITE = 2;
    public static final int REQUEST_INTERNET_CONNECTION = 3;

    // Prompt for Google speech to text
    public static final String ENGLISH_SPEECH_RECOGNIZER_PROMPT = "English word or sentence";
    public static final String PORTUGUESE_SPEECH_RECOGNIZER_PROMPT = "Portuguese word";

    // Splash screen timer
    public static final int SPLASH_SCREEN_TIMER = 3500;
}
