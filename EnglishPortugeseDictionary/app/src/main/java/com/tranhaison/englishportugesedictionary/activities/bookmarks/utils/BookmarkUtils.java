package com.tranhaison.englishportugesedictionary.activities.bookmarks.utils;

import android.content.Context;
import android.content.Intent;

import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.activities.DetailActivity;

public class BookmarkUtils {

    /**
     * Call intent to Detail Activity
     * @param wordList_id
     * @param dictionary_type
     * @param context
     */
    public static void goToDetailActivity(int wordList_id, int dictionary_type, Context context) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.WORD_LIST_ID, wordList_id);
        intent.putExtra(Constants.DICTIONARY_TYPE, dictionary_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
