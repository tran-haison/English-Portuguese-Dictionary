package com.tranhaison.englishportugesedictionary.fragments.mainactivity;

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

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment {

    // Init Views and Adapter
    ListView listViewSearch;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> suggestionList;

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

        // Init array list
        suggestionList = new ArrayList<>();

        // Get data list from MainActivity
        getSearchList();

        // Set adapter to List View
        listViewSearch = view.findViewById(R.id.listViewSearch);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, suggestionList);
        listViewSearch.setAdapter(arrayAdapter);

        // List View item clicked
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (fragmentListener != null)
                    fragmentListener.onItemClick(suggestionList.get(position));
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
     * Get data source from MainActivity
     */
    public void getSearchList() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            suggestionList = bundle.getStringArrayList(Constants.SUGGESTION_LIST);
        }
    }

    /**
     * Reset data source
     */
    public void resetDataSource() {
        // Clear list
        suggestionList.clear();

        // Get new data source and display with list view
        getSearchList();
        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, suggestionList);
        listViewSearch.setAdapter(arrayAdapter);
    }

}