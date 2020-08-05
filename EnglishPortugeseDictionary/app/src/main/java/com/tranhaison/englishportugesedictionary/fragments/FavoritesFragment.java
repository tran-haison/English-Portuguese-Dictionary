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

import com.tranhaison.englishportugesedictionary.FavoriteAdapter;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    // Init List View, Adapter and Array
    ListView lisViewFavorites;
    FavoriteAdapter favoriteAdapter;
    ArrayList<String> wordList;

    // Init Fragment
    private FragmentListener fragmentListener;

    public FavoritesFragment() {
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

        // Get all favorite words then load into List View
        lisViewFavorites = view.findViewById(R.id.lisViewFavorites);
        wordList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(getContext(), wordList);
        lisViewFavorites.setAdapter(favoriteAdapter);

        wordList = addWords();
        favoriteAdapter.notifyDataSetChanged();

        favoriteAdapter.setOnItemClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                if (fragmentListener != null) {
                    fragmentListener.onItemClick((String) favoriteAdapter.getItem(position));
                }
            }
        });

        favoriteAdapter.setOnItemDeleteClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                String value = (String) favoriteAdapter.getItem(position);
                Toast.makeText(getContext(), value + " item is deleted", Toast.LENGTH_SHORT).show();
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
    private ArrayList<String> addWords() {
        ArrayList<String> favorite_list = new ArrayList<>();
        favorite_list.add("hello");
        favorite_list.add("how");
        favorite_list.add("are");
        favorite_list.add("you");
        favorite_list.add("today");

        return favorite_list;
    }
}