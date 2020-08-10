package com.tranhaison.englishportugesedictionary.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.DictionaryWord;
import com.tranhaison.englishportugesedictionary.adapters.WordAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.LoadDatabase;
import com.tranhaison.englishportugesedictionary.fragments.ExampleFragment;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.adapters.ViewPagerAdapter;
import com.tranhaison.englishportugesedictionary.fragments.AntonymFragment;
import com.tranhaison.englishportugesedictionary.fragments.DefinitionFragment;
import com.tranhaison.englishportugesedictionary.fragments.SynonymFragment;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    // Init Views
    ImageButton ibBack, ibFavorite, ibVoiceSearch;
    EditText etSearch;
    TabLayout tabLayout;
    ViewPager viewPagerContainer;

    // Init Adapter
    ViewPagerAdapter viewPagerAdapter;
    DatabaseHelper databaseHelper;

    // Init Fragments
    DefinitionFragment definitionFragment;
    SynonymFragment synonymFragment;
    AntonymFragment antonymFragment;
    ExampleFragment exampleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);

        // Map Views from layout file
        mapViews();

        // Open database
        initDatabase();

        // Init Fragments
        initFragments();

        // Setup ViewPager and TabLayout
        setUpViewPager();

        // Get a word from MainActivity
        String word = getWordFromActivity();

        // Handle events
        setSpeechRecognizer();
        setSearchTextChanged();
        addToFavorite(word);
        returnToPreviousActivity();

    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        ibBack = findViewById(R.id.ibBack);
        ibFavorite = findViewById(R.id.ibFavorite);
        ibVoiceSearch = findViewById(R.id.ibVoiceSearch);
        etSearch = findViewById(R.id.etSearch);
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerContainer = findViewById(R.id.viewPagerContainer);
    }

    /**
     * Open database if already exists
     * else create a new database
     */
    private void initDatabase() {
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
     * Init new Fragments and set default fragment to DefinitionFragment
     */
    private void initFragments() {
        definitionFragment = new DefinitionFragment();
        synonymFragment = new SynonymFragment();
        antonymFragment = new AntonymFragment();
        exampleFragment = new ExampleFragment();

        // Set default fragment
        //goToFragment(definitionFragment);
    }

    /**
     * Add Fragments to ViewPagerAdapter and display with TabLayout
     */
    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments
        viewPagerAdapter.addFragment(definitionFragment, "Definition");
        viewPagerAdapter.addFragment(exampleFragment, "Example");
        viewPagerAdapter.addFragment(synonymFragment, "Synonym");
        viewPagerAdapter.addFragment(antonymFragment, "Antonym");

        // Adapter setup
        viewPagerContainer.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPagerContainer);
    }

    /**
     * Get a word from MainActivity
     * @return
     */
    private String getWordFromActivity() {
        Intent intent = getIntent();

        String word = intent.getStringExtra("search_word");
        if (word != null) {
            // Set word to etSearch
            etSearch.setText(word);

            DictionaryWord dictionaryWord = databaseHelper.getWord(word);

            if (dictionaryWord != null) {
                // Add a word to list of recent words
                databaseHelper.insertHistory(dictionaryWord);

                // Check to see if this word is already in list of Favorite
                // then set ivFavorite to the corresponding state
                DictionaryWord isFavoriteWord = databaseHelper.getFavorite(word);
                if (isFavoriteWord != null) {
                    ibFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                } else {
                    ibFavorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                }

                return word;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Speech to text
     */
    private void setSpeechRecognizer() {
        ibVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call speech intent
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_recognizer);
                startActivityForResult(speechIntent, Constants.REQUEST_SPEECH_RECOGNIZER);
            }
        });
    }

    /**
     * Handle search action event
     */
    private void setSearchTextChanged() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Add a word to list of favorite words
     */
    private void addToFavorite(final String word) {
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (word == null) {
                    Toast.makeText(DetailActivity.this, "Cannot add a meaningless word", Toast.LENGTH_SHORT).show();
                } else {
                    DictionaryWord isFavoriteWord = databaseHelper.getFavorite(word);
                    // If a word is already in Favorite -> remove
                    // else add to list of favorite words
                    if (isFavoriteWord != null) {
                        databaseHelper.deleteFavorite(word);
                        ibFavorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                        Toast.makeText(DetailActivity.this, word + " is removed from Favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        DictionaryWord dictionaryWord = databaseHelper.getWord(word);
                        databaseHelper.insertFavorite(dictionaryWord);
                        ibFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                        Toast.makeText(DetailActivity.this, word + " is added to Favorite", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * ibBack clicked
     */
    private void returnToPreviousActivity() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get text from speech
        if (requestCode == Constants.REQUEST_SPEECH_RECOGNIZER && resultCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            etSearch.setText(matches.get(0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Begin transaction, switch fragments among others
     * @param fragment
     */
    /*private void goToFragment(Fragment fragment) {
        // Remove previous fragment (if any)
        if (getSupportFragmentManager().findFragmentById(R.id.viewPagerContainer) != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.viewPagerContainer))
                    .commit();
        }

        // Replace by another fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.viewPagerContainer, fragment)
                .commit();
    }*/

}