package com.tranhaison.englishportugesedictionary.fragments.mainactivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.adapters.FavoriteAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

public class FavoriteFragment extends Fragment {

    // Init list view, adapter
    ListView lisViewFavorites;
    FavoriteAdapter favoriteAdapter;

    // Init database helper
    DatabaseHelper databaseHelper;

    // Init Fragment listener
    private FragmentListener fragmentListener;

    public FavoriteFragment(DatabaseHelper databaseHelper) {
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set Adapter to List View
        lisViewFavorites = view.findViewById(R.id.listViewFavorites);
        favoriteAdapter = new FavoriteAdapter(getActivity(), databaseHelper);
        lisViewFavorites.setAdapter(favoriteAdapter);

        // List View's item clicked
        favoriteAdapter.setOnItemClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                if (fragmentListener != null) {
                    BookmarkWord favoriteWord = (BookmarkWord) favoriteAdapter.getItem(position);
                    fragmentListener.onItemClick(favoriteWord);
                }
            }
        });

        // List View's item removed from list of favorite
        favoriteAdapter.setOnItemDeleteClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                // Get word
                BookmarkWord dictionaryWord = (BookmarkWord) favoriteAdapter.getItem(position);
                String word = dictionaryWord.getDisplayWord();

                // Delete word from Favorite
                databaseHelper.deleteFavorite(dictionaryWord.getWordList_id());
                favoriteAdapter.removeWord(position);
                favoriteAdapter.notifyDataSetChanged();

                // Inform user
                Toast.makeText(getActivity(), word + " is removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Event handler
     *
     * @param fragmentListener
     */
    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

}