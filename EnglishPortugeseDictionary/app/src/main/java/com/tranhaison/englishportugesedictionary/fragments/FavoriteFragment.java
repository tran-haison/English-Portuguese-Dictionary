package com.tranhaison.englishportugesedictionary.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.DictionaryWord;
import com.tranhaison.englishportugesedictionary.adapters.FavoriteAdapter;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    // Init List View, Adapter and Array
    ListView lisViewFavorites;
    FavoriteAdapter favoriteAdapter;
    ArrayList<DictionaryWord> favoriteList;

    // Init Fragment listener
    private FragmentListener fragmentListener;

    public FavoriteFragment() {
        // Required empty public constructor
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

        // Get favoriteList data source from MainActivity
        getFavoriteList();

        // Set Adapter to List View
        lisViewFavorites = view.findViewById(R.id.listViewFavorites);
        favoriteAdapter = new FavoriteAdapter(getActivity(), favoriteList);
        lisViewFavorites.setAdapter(favoriteAdapter);

        // List View's item clicked
        favoriteAdapter.setOnItemClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                if (fragmentListener != null) {
                    DictionaryWord favoriteWord = (DictionaryWord) favoriteAdapter.getItem(position);
                    String word = favoriteWord.getWord();
                    fragmentListener.onItemClick(word);
                }
            }
        });

        // List View's item removed from list of favorite
        favoriteAdapter.setOnItemDeleteClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), favoriteAdapter.getItem(position) + " is removed from favorites", Toast.LENGTH_SHORT).show();
                favoriteAdapter.removeWord(position);
                favoriteAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Event handler
     * @param fragmentListener
     */
    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    /**
     * Get a List of favorite words in db
     * @return
     */
    public void getFavoriteList() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            favoriteList = (ArrayList<DictionaryWord>) bundle.getSerializable("favorite_list");
        }
    }
}