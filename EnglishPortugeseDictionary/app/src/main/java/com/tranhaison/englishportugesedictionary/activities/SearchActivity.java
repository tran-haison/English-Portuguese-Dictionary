package com.tranhaison.englishportugesedictionary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.utils.CountryRegion;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.utils.LoadDatabase;
import com.tranhaison.englishportugesedictionary.utils.Utils;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    // Views
    ImageButton ibBackFromSearch, ibSearch;
    EditText etSearch;
    ListView listViewSearch;
    ImageView ivDictionaryType;
    LinearLayout linearLayoutSearchPrompt;

    // Adapter for list view search
    ArrayAdapter<String> suggestedAdapter;
    ArrayList<String> suggestedList;

    // Database
    DatabaseHelper databaseHelper;

    // Init variables to get current dictionary type (default = ENG - POR)
    private int dictionary_type = Constants.ENG_POR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Map Views from layout file
        mapViews();

        // Init database
        initDatabase();

        // Set initial value to Views
        etSearch.requestFocus();
        CountryRegion.setCountryFlagIcon(this);
        initListView();
        setViewVisibility();

        // Handle events
        setQueryTextChanged();
        searchKeyboardConfirmed();
        listViewItemClicked();
    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        ibBackFromSearch = findViewById(R.id.ibBackFromSearch);
        ibSearch = findViewById(R.id.ibSearch);
        etSearch = findViewById(R.id.etSearch);
        listViewSearch = findViewById(R.id.listViewSearch);
        ivDictionaryType = findViewById(R.id.ivDictionaryType);
        linearLayoutSearchPrompt = findViewById(R.id.linearLayoutSearchPrompt);
    }

    /**
     * Open database if already exists
     * else create a new database
     */
    public void initDatabase() {
        databaseHelper = new DatabaseHelper(this);

        // If the database already exists -> open it
        // else create new database and open it
        if (databaseHelper.checkDatabase()) {
            databaseHelper.openDatabase();
        } else {
            LoadDatabase loadDatabase = new LoadDatabase(this, databaseHelper);
            loadDatabase.execute();
        }
    }

    /**
     * Init list view with array list and adapter
     */
    private void initListView() {
        suggestedList = new ArrayList<>();
        suggestedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestedList);
        listViewSearch.setAdapter(suggestedAdapter);
    }

    private void setViewVisibility() {
        if (suggestedList == null || suggestedList.size() <= 0) {
            listViewSearch.setVisibility(View.GONE);
            linearLayoutSearchPrompt.setVisibility(View.VISIBLE);
        } else {
            listViewSearch.setVisibility(View.VISIBLE);
            linearLayoutSearchPrompt.setVisibility(View.GONE);
        }
    }

    /**
     * Swap dictionary type between English and Portuguese
     * @param view
     */
    public void swapDictionaryType(View view) {
        if (dictionary_type == Constants.ENG_POR) {
            dictionary_type = Constants.POR_ENG;
            etSearch.setHint(getString(R.string.portuguese_word));
            ivDictionaryType.setImageResource(CountryRegion.getCountryFlagIcon());
        } else if (dictionary_type == Constants.POR_ENG) {
            dictionary_type = Constants.ENG_POR;
            etSearch.setHint(getString(R.string.english_word));
            ivDictionaryType.setImageResource(R.drawable.img_england_flag);
        }

        // Reset suggested list and list view if edit text already contain word
        String word = etSearch.getText().toString();
        if (!word.isEmpty()) {
            resetSuggestedList(word);
        }
    }

    /**
     * Display suggested list view when input text changed
     */
    private void setQueryTextChanged() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString();

                if (!word.isEmpty()) {
                    resetSuggestedList(word);
                }

                setViewVisibility();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Reset suggested list
     * then init new adapter and set to list view
     * @param word
     */
    private void resetSuggestedList(String word) {
        if (suggestedList != null) {
            suggestedList.clear();
        }

        // Get suggested list from db
        suggestedList = databaseHelper.getSuggestions(word, dictionary_type);
        if (suggestedList != null) {
            suggestedAdapter = new ArrayAdapter(SearchActivity.this, android.R.layout.simple_list_item_1, suggestedList);
            listViewSearch.setAdapter(suggestedAdapter);
        }
    }

    /**
     * Search button confirmed
     */
    private void searchKeyboardConfirmed() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void searchButtonClicked(View view) {
        performSearch();
    }

    /**
     * Go to Detail Activity when search button clicked
     */
    private void performSearch() {
        if (!etSearch.getText().toString().isEmpty()) {
            goToDetailActivity(etSearch.getText().toString(), dictionary_type);
        } else {
            Toast.makeText(this, getString(R.string.please_enter_a_word), Toast.LENGTH_SHORT).show();
        }
    }

    private void listViewItemClicked() {
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                goToDetailActivity(suggestedList.get(position), dictionary_type);
            }
        });
    }

    /**
     * Call intent to Detail Activity
     *
     * @param word
     */
    private void goToDetailActivity(String word, int dictionary_type) {
        // Get id of the word
        int wordList_id = databaseHelper.getWordListId(word, dictionary_type);

        // If word exists -> go to DetailActivity
        // else go to OnlineSearchingActivity
        if (wordList_id != -1) {
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            intent.putExtra(Constants.WORD_LIST_ID, wordList_id);
            intent.putExtra(Constants.DICTIONARY_TYPE, dictionary_type);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.word_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Return to Main Activity when back button clicked
     * @param view
     */
    public void returnFromSearchActivity(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}