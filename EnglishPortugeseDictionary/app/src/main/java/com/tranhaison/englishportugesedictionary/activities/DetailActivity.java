package com.tranhaison.englishportugesedictionary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.utils.CountryRegion;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.GoogleTextToSpeech;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.LocalTextToSpeech;
import com.tranhaison.englishportugesedictionary.utils.Utils;
import com.tranhaison.englishportugesedictionary.adapters.detailactivity.ViewPagerAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.utils.LoadDatabase;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.fragments.DefinitionFragment;
import com.tranhaison.englishportugesedictionary.fragments.ExampleFragment;
import com.tranhaison.englishportugesedictionary.fragments.ExplanationFragment;
import com.tranhaison.englishportugesedictionary.fragments.SynonymFragment;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    // Init Views and Layouts
    ImageButton ibBack, ibFavorite, ibVoiceSearch, ibSearchOnline, ibSearchInDetail, ibCloseSearchDetail;
    TabLayout tabLayout;
    ViewPager viewPagerContainer;
    ImageView ivFlagDictionaryType;
    ListView listViewSuggestion;
    LinearLayout linearLayoutDetail, linearLayoutToolBarIcon;
    TextView tvWordDetail;
    RelativeLayout relativeLayoutSearchDetail;
    EditText etSearchDetail;
    ViewPagerAdapter viewPagerAdapter;

    // Init text to speech
    LocalTextToSpeech localTextToSpeech;
    GoogleTextToSpeech googleTextToSpeech;

    // Init Fragments
    DefinitionFragment definitionFragment;
    SynonymFragment synonymFragment;
    ExplanationFragment explanationFragment;
    ExampleFragment exampleFragment;

    // Init global variables
    private int dictionary_type, synonym_type, flag_type;
    private DictionaryWord dictionaryWord;
    DatabaseHelper databaseHelper;

    ArrayAdapter<String> suggestionAdapter;
    ArrayList<String> suggestionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Map Views from layout file
        mapViews();

        // Open database
        openDatabase();

        // Get a word from MainActivity
        getDataFromActivity();
        CountryRegion.setCountryFlagIcon(this);

        // Init tts instances
        initTextToSpeech();

        // Fragments
        initFragments();
        handleFragmentEvents();

        // Setup ViewPager and TabLayout
        setUpViewPager();

        // Handle events
        setSpeechRecognizer();
        setSearchAction();
        setViewVisibility();
        changeFlagType();
        addToFavorite();
        returnToPreviousActivity();
        goToOnlineSearchingActivity();
    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        ibBack = findViewById(R.id.ibBack);
        ibFavorite = findViewById(R.id.ibFavorite);
        ibVoiceSearch = findViewById(R.id.ibVoiceSearch);
        ibSearchOnline = findViewById(R.id.ibSearchOnline);
        ibSearchInDetail = findViewById(R.id.ibSearchInDetail);
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerContainer = findViewById(R.id.viewPagerContainer);
        ivFlagDictionaryType = findViewById(R.id.ivFlagDictionaryType);
        listViewSuggestion = findViewById(R.id.listViewSuggestion);
        linearLayoutDetail = findViewById(R.id.linearLayoutDetail);
        linearLayoutToolBarIcon = findViewById(R.id.linearLayoutToolBarIcon);
        tvWordDetail = findViewById(R.id.tvWordDetail);
        ibCloseSearchDetail = findViewById(R.id.ibCloseSearchDetail);
        relativeLayoutSearchDetail = findViewById(R.id.relativeLayoutSearchDetail);
        etSearchDetail = findViewById(R.id.etSearchDetail);
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
    private boolean getDataFromActivity() {
        Intent intent = getIntent();

        // Get data from MainActivity
        dictionary_type = intent.getIntExtra(Constants.DICTIONARY_TYPE, Constants.ENG_POR);
        int wordList_id = intent.getIntExtra(Constants.WORD_LIST_ID, -1);

        // Set synonym type opposite to dictionary type
        if (dictionary_type == Constants.ENG_POR) {
            synonym_type = Constants.POR_ENG;
        } else if (dictionary_type == Constants.POR_ENG) {
            synonym_type = Constants.ENG_POR;
        }

        if (wordList_id != -1) {
            // Get information of a word
            dictionaryWord = databaseHelper.getWord(wordList_id, dictionary_type);
            String word = dictionaryWord.getDisplayWord();
            String explanation = dictionaryWord.getExplanations();

            // Add a word to list of recent words
            databaseHelper.insertHistory(wordList_id, word, explanation, dictionary_type);

            // Set the favorite image based on the id of word
            setFavoriteImage(wordList_id);

            // Set text to tv
            tvWordDetail.setText(word);

            // Set flag based on dictionary type
            if (dictionary_type == Constants.ENG_POR) {
                flag_type = Constants.ENG_POR;
                ivFlagDictionaryType.setImageResource(R.drawable.img_england_flag);
            } else if (dictionary_type == Constants.POR_ENG) {
                flag_type = Constants.POR_ENG;
                ivFlagDictionaryType.setImageResource(R.drawable.img_portugal_flag);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Check to see if this word is already in list of Favorite
     * then set ivFavorite to the corresponding state
     *
     * @param wordList_id
     */
    private void setFavoriteImage(int wordList_id) {
        BookmarkWord isFavoriteWord = databaseHelper.getFavorite(wordList_id);
        if (isFavoriteWord != null) {
            ibFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            ibFavorite.setImageResource(R.drawable.ic_favorite_border_24dp);
        }
    }

    /**
     * Init TTS instance with language depending on the dictionary type
     */
    private void initTextToSpeech() {
        // Online text to speech audio
        googleTextToSpeech = new GoogleTextToSpeech(this);

        // Local text to speech audio
        localTextToSpeech = new LocalTextToSpeech(this);
        localTextToSpeech.initialize();
    }

    /**
     * Init new Fragments and set default fragment to DefinitionFragment
     */
    private void initFragments() {
        definitionFragment = new DefinitionFragment(databaseHelper, dictionaryWord, dictionary_type,
                localTextToSpeech, googleTextToSpeech);

        explanationFragment = new ExplanationFragment(databaseHelper, dictionary_type, dictionaryWord,
                localTextToSpeech, googleTextToSpeech);

        exampleFragment = new ExampleFragment(databaseHelper, dictionaryWord.getWordList_id(), dictionaryWord.getDisplayWord(),
                dictionary_type, localTextToSpeech, googleTextToSpeech);

        synonymFragment = new SynonymFragment(databaseHelper, dictionaryWord, dictionary_type,
                localTextToSpeech, googleTextToSpeech);

    }

    /**
     * Override method to handle each event of each fragments
     */
    private void handleFragmentEvents() {
        synonymFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                if (synonym_type == Constants.POR_ENG) {
                    synonym_type = Constants.ENG_POR;
                    dictionary_type = Constants.POR_ENG;
                } else if (synonym_type == Constants.ENG_POR) {
                    synonym_type = Constants.POR_ENG;
                    dictionary_type = Constants.ENG_POR;
                }

                goToDetailActivity(value);
            }

            @Override
            public void onItemClick(BookmarkWord favoriteWord) {

            }
        });
    }

    /**
     * Add Fragments to ViewPagerAdapter and display with TabLayout
     */
    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments
        viewPagerAdapter.addFragment(definitionFragment, getString(R.string.definition));
        viewPagerAdapter.addFragment(explanationFragment, getString(R.string.explanation));
        viewPagerAdapter.addFragment(exampleFragment, getString(R.string.example));
        viewPagerAdapter.addFragment(synonymFragment, getString(R.string.synonym));

        // Adapter setup
        viewPagerContainer.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPagerContainer);
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

                if (flag_type == Constants.ENG_POR) {
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString());
                    speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, Constants.ENGLISH_SPEECH_RECOGNIZER_PROMPT);
                } else {
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("pt", "POR").toString());
                    speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, Constants.PORTUGUESE_SPEECH_RECOGNIZER_PROMPT);
                }
                //speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(speechIntent, Constants.REQUEST_SPEECH_RECOGNIZER);
            }
        });
    }

    /**
     * Handle search action event
     */
    private void setSearchAction() {
        etSearchDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();

                if (newText.isEmpty()) {
                    listViewSuggestion.setVisibility(View.GONE);
                } else {
                    listViewSuggestion.setVisibility(View.VISIBLE);

                    // Get suggestions each time the input text is changed
                    resetSuggestedList(newText);

                    // List view item clicked
                    // Get the word and pass to DetailActivity
                    listViewSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (flag_type == Constants.ENG_POR) {
                                dictionary_type = Constants.ENG_POR;
                                synonym_type = Constants.POR_ENG;
                            } else if (flag_type == Constants.POR_ENG) {
                                dictionary_type = Constants.POR_ENG;
                                synonym_type = Constants.ENG_POR;
                            }

                            goToDetailActivity(suggestionList.get(i));
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etSearchDetail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (flag_type == Constants.ENG_POR) {
                        dictionary_type = Constants.ENG_POR;
                        synonym_type = Constants.POR_ENG;
                    } else if (flag_type == Constants.POR_ENG) {
                        dictionary_type = Constants.POR_ENG;
                        synonym_type = Constants.ENG_POR;
                    }

                    String word = etSearchDetail.getText().toString();
                    goToDetailActivity(word);

                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Set visibility to views when search bar is expanded or collapsed
     */
    private void setViewVisibility() {
        // Display search bar and hide icons
        ibSearchInDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchBar();
                etSearchDetail.requestFocus();
                Utils.openKeyboard(etSearchDetail, DetailActivity.this);
            }
        });

        // Close the search bar and display icons
        ibCloseSearchDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etSearchDetail.getText().toString().isEmpty()) {
                    etSearchDetail.getText().clear();
                } else {
                    Utils.closeKeyboard(DetailActivity.this);
                    hideSearchBar();
                }
            }
        });
    }

    private void showSearchBar() {
        ibVoiceSearch.setVisibility(View.GONE);
        ibFavorite.setVisibility(View.GONE);
        ibSearchOnline.setVisibility(View.GONE);
        ibSearchInDetail.setVisibility(View.GONE);
        relativeLayoutSearchDetail.setVisibility(View.VISIBLE);
    }

    private void hideSearchBar() {
        ibVoiceSearch.setVisibility(View.VISIBLE);
        ibFavorite.setVisibility(View.VISIBLE);
        ibSearchOnline.setVisibility(View.VISIBLE);
        ibSearchInDetail.setVisibility(View.VISIBLE);
        relativeLayoutSearchDetail.setVisibility(View.GONE);
    }

    /**
     * Get suggested list of current word in search bar
     *
     * @param word
     */
    private void resetSuggestedList(String word) {
        suggestionList.clear();
        suggestionList = databaseHelper.getSuggestions(word, flag_type);
        suggestionAdapter = new ArrayAdapter(DetailActivity.this, android.R.layout.simple_list_item_1, suggestionList);
        listViewSuggestion.setAdapter(suggestionAdapter);
    }

    /**
     * Change dictionary type from ENG-POR to POR-ENG and vice versa
     */
    private void changeFlagType() {
        ivFlagDictionaryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change dictionary type
                if (flag_type == Constants.ENG_POR) {
                    flag_type = Constants.POR_ENG;
                    ivFlagDictionaryType.setImageResource(CountryRegion.getCountryFlagIcon());
                } else if (flag_type == Constants.POR_ENG) {
                    flag_type = Constants.ENG_POR;
                    ivFlagDictionaryType.setImageResource(R.drawable.img_england_flag);
                }

                // Reset suggested list if currently searching for a word
                String word = etSearchDetail.getText().toString();
                if (!word.isEmpty()) {
                    resetSuggestedList(word);
                }
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
                    Toast.makeText(DetailActivity.this, displayWord + getString(R.string.is_removed_from_favorite), Toast.LENGTH_SHORT).show();
                } else {
                    // Add a word to list of Favorite
                    databaseHelper.insertFavorite(wordList_id, displayWord, explanation, dictionary_type);

                    // Set img to ibFavorite
                    ibFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                    Toast.makeText(DetailActivity.this, displayWord + getString(R.string.is_added_to_favorite), Toast.LENGTH_SHORT).show();
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

    /**
     * Call intent and pass data through Detail Activity
     *
     * @param word
     */
    private void goToDetailActivity(String word) {
        if (word.isEmpty()) {
            Toast.makeText(DetailActivity.this, getString(R.string.please_enter_a_word), Toast.LENGTH_SHORT).show();
        } else {
            // Get id of the word
            final int wordList_id = databaseHelper.getWordListId(word, dictionary_type);

            if (wordList_id != -1) {
                // Get new word
                dictionaryWord = databaseHelper.getWord(wordList_id, dictionary_type);

                // Update views
                tvWordDetail.setText(word);
                etSearchDetail.getText().clear();
                tabLayout.selectTab(tabLayout.getTabAt(0));

                Utils.closeKeyboard(DetailActivity.this);
                hideSearchBar();
                setFavoriteImage(wordList_id);

                // Insert word to History
                databaseHelper.insertHistory(wordList_id, word, dictionaryWord.getExplanations(), dictionary_type);

                // Update fragment within view pager
                viewPagerAdapter.updateData(dictionary_type, wordList_id, word);
                viewPagerAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(DetailActivity.this, getString(R.string.word_not_found_please_search_onnline), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Search online for current word
     */
    private void goToOnlineSearchingActivity() {
        ibSearchOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, OnlineSearchingActivity.class);
                intent.putExtra(Constants.SEARCH_WORD, dictionaryWord.getDisplayWord());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get text from speech
        if (requestCode == Constants.REQUEST_SPEECH_RECOGNIZER && resultCode == RESULT_OK && data != null) {
            // Get text from speech
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String word = matches.get(0);
            word = word.toLowerCase();

            // Set type to each element
            if (flag_type == Constants.ENG_POR) {
                dictionary_type = Constants.ENG_POR;
                synonym_type = Constants.POR_ENG;
            } else if (flag_type == Constants.POR_ENG) {
                dictionary_type = Constants.POR_ENG;
                synonym_type = Constants.ENG_POR;
            }

            // Reload activity with a new word
            goToDetailActivity(word);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (relativeLayoutSearchDetail.getVisibility() == View.VISIBLE) {
            etSearchDetail.getText().clear();
            Utils.closeKeyboard(DetailActivity.this);
            hideSearchBar();
        } else if (tabLayout.getSelectedTabPosition() != 0) {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        // Shutdown all tts instances
        localTextToSpeech.shutdown();

        // Stop tts audio
        googleTextToSpeech.stopPlay();

        // Close db
        databaseHelper.close();

        super.onDestroy();
    }

}