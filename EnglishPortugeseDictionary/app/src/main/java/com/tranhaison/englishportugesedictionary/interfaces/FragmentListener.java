package com.tranhaison.englishportugesedictionary.interfaces;

import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;

public interface FragmentListener {
    void onItemClick(String value);
    void onItemClick(BookmarkWord favoriteWord);
}
