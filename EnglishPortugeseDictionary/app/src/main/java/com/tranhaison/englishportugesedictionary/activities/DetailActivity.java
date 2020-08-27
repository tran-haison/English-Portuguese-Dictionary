package com.tranhaison.englishportugesedictionary.activities;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.adapters.ViewPagerAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.LoadDatabase;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.fragments.detailactivity.DefinitionFragment;
import com.tranhaison.englishportugesedictionary.fragments.detailactivity.ExampleFragment;
import com.tranhaison.englishportugesedictionary.fragments.detailactivity.ExplanationFragment;
import com.tranhaison.englishportugesedictionary.fragments.detailactivity.SynonymFragment;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    // Init Views and Layouts
    ImageButton ibBack, ibFavorite, ibVoiceSearch;
    MaterialSearchBar searchBarDetail;
    TabLayout tabLayout;
    ViewPager viewPagerContainer;
    TextView tvDictionaryWord;
    ImageView ivSpeakerDetail, ivCopyDetail;
    ConstraintLayout constrainLayoutDetail;

    // Init Adapter
    ViewPagerAdapter viewPagerAdapter;

    // Init database helper
    DatabaseHelper databaseHelper;

    // Init text to speech
    TextToSpeech textToSpeech;

    // Init Fragments
    DefinitionFragment definitionFragment;
    SynonymFragment synonymFragment;
    ExplanationFragment explanationFragment;
    ExampleFragment exampleFragment;

    // Init global variables
    private int dictionary_type;
    private DictionaryWord dictionaryWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);

        // Map Views from layout file
        mapViews();

        // Open database
        openDatabase();

        // Get a word from MainActivity
        boolean isExisted = getDataFromMainActivity();

        // Init Fragments
        initFragments();
        handleFragmentEvents();

        // Setup ViewPager and TabLayout
        setUpViewPager();

        // Handle events
        setSpeechRecognizer();
        setSearchAction();
        addToFavorite();
        copyToClipboard();
        returnToPreviousActivity();
        initTextToSpeech();
        speakWord();
    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        ibBack = findViewById(R.id.ibBack);
        ibFavorite = findViewById(R.id.ibFavorite);
        ibVoiceSearch = findViewById(R.id.ibVoiceSearch);
        searchBarDetail = findViewById(R.id.searchBarDetail);
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerContainer = findViewById(R.id.viewPagerContainer);
        tvDictionaryWord = findViewById(R.id.tvDictionaryWord);
        ivSpeakerDetail = findViewById(R.id.ivSpeakerDetail);
        ivCopyDetail = findViewById(R.id.ivCopyDetail);
        constrainLayoutDetail = findViewById(R.id.constrainLayoutDetail);
    }

    /**
     * Open database if already exists
     * else create a new database
     */
    private void openDatabase() {
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
     * Get a word from MainActivity
     * Return true if word exists
     * else return false
     *
     * @return
     */
    private boolean getDataFromMainActivity() {
        Intent intent = getIntent();

        // Get data from MainActivity
        dictionary_type = intent.getIntExtra(Constants.DICTIONARY_TYPE, Constants.ENG_POR);
        int wordList_id = intent.getIntExtra(Constants.WORD_LIST_ID, -1);

        if (wordList_id != -1) {
            // Get information of a word
            dictionaryWord = databaseHelper.getWord(wordList_id, dictionary_type);
            String word = dictionaryWord.getDisplayWord();
            String explanation = dictionaryWord.getExplanations();

            // Set text to views
            tvDictionaryWord.setText(word);

            // Add a word to list of recent words
            databaseHelper.insertHistory(wordList_id, word, explanation, dictionary_type);

            // Check to see if this word is already in list of Favorite
            // then set ivFavorite to the corresponding state
            BookmarkWord isFavoriteWord = databaseHelper.getFavorite(wordList_id);
            if (isFavoriteWord != null) {
                ibFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
            } else {
                ibFavorite.setImageResource(R.drawable.ic_favorite_border_24dp);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Init new Fragments and set default fragment to DefinitionFragment
     */
    private void initFragments() {
        definitionFragment = new DefinitionFragment(databaseHelper, dictionaryWord, dictionary_type);
        synonymFragment = new SynonymFragment(databaseHelper, dictionaryWord, dictionary_type);
        explanationFragment = new ExplanationFragment(databaseHelper, dictionary_type, dictionaryWord);
        exampleFragment = new ExampleFragment(databaseHelper, dictionaryWord);
    }

    /**
     * Add Fragments to ViewPagerAdapter and display with TabLayout
     */
    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments
        viewPagerAdapter.addFragment(definitionFragment, "Definition");
        viewPagerAdapter.addFragment(explanationFragment, "Explain");
        viewPagerAdapter.addFragment(exampleFragment, "Example");
        viewPagerAdapter.addFragment(synonymFragment, "Synonym");

        // Adapter setup
        viewPagerContainer.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPagerContainer);
    }

    /**
     * Override method to handle each event of each fragments
     */
    private void handleFragmentEvents() {
        synonymFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Toast.makeText(DetailActivity.this, value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(BookmarkWord favoriteWord) {

            }
        });
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
     * Init TTS instance with language depending on the dictionary type
     */
    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {

                    int result;
                    if (dictionary_type == Constants.POR_ENG) {
                        result = textToSpeech.setLanguage(new Locale("pt", "POR"));
                    } else {
                        result = textToSpeech.setLanguage(Locale.ENGLISH);
                    }

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(DetailActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "Initialize failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Speak a word when pressing button
     */
    private void speakWord() {
        ivSpeakerDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = dictionaryWord.getDisplayWord();
                textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    /**
     * Handle search action event
     */
    private void setSearchAction() {
        searchBarDetail.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                String word = text.toString();
                goToDetailActivity(word);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });
    }

    /**
     * Add a word to list of favorite words
     */
    private void addToFavorite() {
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get information of a word
                int wordList_id = dictionaryWord.getWordList_id();
                String displayWord = dictionaryWord.getDisplayWord();
                String explanation = dictionaryWord.getExplanations();

                // Get a favorite word in Favorite table
                BookmarkWord isFavoriteWord = databaseHelper.getFavorite(wordList_id);

                // If a word is already in Favorite -> remove
                // else add to list of favorite words
                if (isFavoriteWord != null) {
                    // Remove a word from Favorite
                    databaseHelper.deleteFavorite(wordList_id);

                    // Set image to ibFavorite
                    ibFavorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                    Toast.makeText(DetailActivity.this, displayWord + " is removed from Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    // Add a word to list of Favorite
                    databaseHelper.insertFavorite(wordList_id, displayWord, explanation, dictionary_type);

                    // Set img to ibFavorite
                    ibFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                    Toast.makeText(DetailActivity.this, displayWord + " is added to Favorite", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * Copy a word to clipboard
     */
    private void copyToClipboard() {
        ivCopyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayWord = dictionaryWord.getDisplayWord();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(Constants.CLIPBOARD_LABEL, displayWord);
                clipboard.setPrimaryClip(clip);
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

    /**
     * Call intent and pass data through Detail Activity
     * @param word
     */
    private void goToDetailActivity(String word) {
        if (word.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Please type a word", Toast.LENGTH_SHORT).show();
        } else {
            // Get id of the word
            int wordList_id = databaseHelper.getWordListId(word, dictionary_type);

            if (wordList_id != -1) {
                // Init intent and pass data through it
                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                intent.putExtra(Constants.WORD_LIST_ID, wordList_id);
                intent.putExtra(Constants.DICTIONARY_TYPE, dictionary_type);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                // Init pair to make transition between activity
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(constrainLayoutDetail, "transition_detail");

                // start activity based on the version SDK
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DetailActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }

            } else {
                Toast.makeText(DetailActivity.this, "Word not found, please search online", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get text from speech
        if (requestCode == Constants.REQUEST_SPEECH_RECOGNIZER && resultCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String word = matches.get(0);
            goToDetailActivity(word);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        databaseHelper.close();
        super.onDestroy();
    }


}