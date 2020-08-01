package com.tranhaison.englishportugesedictionary.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tranhaison.englishportugesedictionary.FragmentListener;
import com.tranhaison.englishportugesedictionary.R;

public class SearchFragment extends Fragment {

    ListView listViewSearch;
    ArrayAdapter<String> arrayAdapter;

    private FragmentListener fragmentListener;

    public SearchFragment() {
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewSearch = view.findViewById(R.id.listViewSearch);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getWordList());

        listViewSearch.setAdapter(arrayAdapter);
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (fragmentListener != null)
                    fragmentListener.onItemClick(getWordList()[position]);
            }
        });
    }

    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    private String[] getWordList() {
        String[] wordList = new String[] {
                "a",
                "abc",
                "abandon",
                "about",
                "above",
                "abuse"
        };
        return wordList;
    }






}