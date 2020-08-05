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

import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchFragment extends Fragment {

    // Init Views and Adapter
    ListView listViewSearch;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> searchList;

    // Init fragment listener to pass argument to Main Activity
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

        // Init search array list
        searchList = new ArrayList<>();
        searchList = getSearchList();

        // Set adapter to List View
        listViewSearch = view.findViewById(R.id.listViewSearch);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchList);
        listViewSearch.setAdapter(arrayAdapter);

        // List View item clicked
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (fragmentListener != null)
                    fragmentListener.onItemClick(searchList.get(position));
            }
        });
    }

    /**
     * Event handler: parameter is passed from Main Activity to this Fragment
     * @param fragmentListener
     */
    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    /**
     * Get the list of words start with @value and set to List View
     * @param value
     */
    public void filterValue(String value) {
        arrayAdapter.getFilter().filter(value);
        int size = arrayAdapter.getCount();
        for (int i=0; i<size; i++) {
            if (arrayAdapter.getItem(i).startsWith(value)) {
                listViewSearch.setSelection(i);
                break;
            }
        }
    }
    /**
     * Return a list of all words
     * @return
     */
    public ArrayList<String> getSearchList() {
        String[] list = new String[] {
                "a",
                "abc",
                "abandon",
                "about",
                "above",
                "abuse"
        };

        ArrayList<String> wordList = new ArrayList<>();
        wordList.addAll(Arrays.asList(list));

        return wordList;
    }

}