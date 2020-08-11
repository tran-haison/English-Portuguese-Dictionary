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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.DictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HistoryFragment extends Fragment {

    // Init Views and Adapter
    Button btnClearHistory;
    ListView listViewHistory;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> historyStringList;
    ArrayList<DictionaryWord> historyWordList;

    // Init fragment listener to pass argument to Main Activity
    private FragmentListener fragmentListener;

    public HistoryFragment() {
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map Views
        btnClearHistory = view.findViewById(R.id.btnClearHistory);
        listViewHistory = view.findViewById(R.id.listViewHistory);

        // Init array
        historyStringList = new ArrayList<>();
        historyWordList = new ArrayList<>();

        // Get data
        getHistoryList();

        // Set adapter to List View
        arrayAdapter = new ArrayAdapter<> (getContext(), android.R.layout.simple_list_item_1, historyStringList);
        listViewHistory.setAdapter(arrayAdapter);

        // List View item clicked
        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (fragmentListener != null) {
                    fragmentListener.onItemClick(historyStringList.get(i));
                }
            }
        });

        // Button clicked
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear all words in list in case list is not empty
                if (!historyWordList.isEmpty()) {
                    historyStringList.clear();
                    historyWordList.clear();
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Clear all history", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Empty list", Toast.LENGTH_SHORT).show();
                }
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
     * Get a list of all words that have been searched
     */
    public void getHistoryList() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            historyWordList = (ArrayList<DictionaryWord>) bundle.getSerializable("history_list");
        }
        convertToStringArrayList();
    }

    /**
     * Get a list of all words only (not include definition, example, synonym and antonym)
     */
    public void convertToStringArrayList() {
        for (DictionaryWord dictionaryWord : historyWordList) {
            String word = dictionaryWord.getWord();
            historyStringList.add(word);
        }
    }

    /**
     * Reset the list of words in History
     */
    public void resetDataSource() {
        historyWordList.clear();
        historyStringList.clear();

        getHistoryList();
        arrayAdapter.notifyDataSetChanged();
        //arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, historyStringList);
        //listViewHistory.setAdapter(arrayAdapter);
    }
}