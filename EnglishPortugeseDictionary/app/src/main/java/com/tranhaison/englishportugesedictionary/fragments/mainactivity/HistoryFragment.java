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

import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    // Init Views and Adapter
    Button btnClearHistory;
    ListView listViewHistory;
    ArrayAdapter<String> arrayAdapter;

    // Init array lists
    ArrayList<String> historyStringList;
    ArrayList<BookmarkWord> historyList;

    // Init database helper
    DatabaseHelper databaseHelper;

    // Init fragment listener to pass argument to Main Activity
    private FragmentListener fragmentListener;

    public HistoryFragment(DatabaseHelper databaseHelper) {
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
        historyList = new ArrayList<>();

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
                    fragmentListener.onItemClick(historyList.get(i));
                }
            }
        });

        // Button clicked
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear all words in list in case list is not empty
                if (!historyList.isEmpty()) {
                    // Delete all in History
                    databaseHelper.deleteAllHistory();
                    historyStringList.clear();
                    historyList.clear();
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
     * Get a list of all recent BookmarkWord and convert to list of all words (just string)
     */
    public void getHistoryList() {
        historyList = databaseHelper.getAllHistory();
        for (BookmarkWord recentWord : historyList) {
            historyStringList.add(recentWord.getDisplayWord());
        }
    }


}