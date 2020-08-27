package com.tranhaison.englishportugesedictionary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.datatransport.BuildConfig;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.DictionaryState;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.LoadDatabase;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.DictionaryWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.AboutFragment;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.FavoriteFragment;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.HelpFragment;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.HistoryFragment;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.MainFragment;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.SearchFragment;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Init Views and Layouts
    LinearLayout linearLayoutMain;
    DrawerLayout drawerLayout;
    FrameLayout frameLayoutContainerGeneral;
    NavigationView navigationView;
    ImageButton ibMenu, ibVoiceSearchMain;
    ImageView ivDictionaryType;
    MaterialSearchBar searchBarMain;

    // Init Fragments
    MainFragment mainFragment;
    FavoriteFragment favoriteFragment;
    HistoryFragment historyFragment;
    SearchFragment searchFragment;
    HelpFragment helpFragment;
    AboutFragment aboutFragment;

    // Init Database Helper and Adapter
    DatabaseHelper databaseHelper;

    // Init variables to hold current dictionary type (default = ENG - POR)
    private int dictionary_type = Constants.ENG_POR;
    private ArrayList<String> suggestionList;

    public MutableLiveData<List<String>> availableModels = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Map Views from layout file
        mapViews();

        // Init Database
        initDatabase();

        // Init and handle Fragments's event
        initFragments();
        handleFragmentsEvent();

        // Get the latest dictionary state
        getLatestState();

        // Handle events for Views and Menu
        showPopUpMenu();
        setButtonClicked();
        setupNavigationDrawer();
        setSearchTextChange();
        setSearchAction();

    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        linearLayoutMain = findViewById(R.id.linearLayoutMain);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        frameLayoutContainerGeneral = findViewById(R.id.frameLayoutContainerGeneral);
        ibMenu = findViewById(R.id.ibMenu);
        ibVoiceSearchMain = findViewById(R.id.ibVoiceSearchMain);
        ivDictionaryType = findViewById(R.id.ivDictionaryType);
        searchBarMain = findViewById(R.id.searchBarMain);
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
     * Get instances of Fragments
     * Put 1st Fragment into stack
     * Set visibility to Fragment and Views
     */
    private void initFragments() {
        mainFragment = new MainFragment(databaseHelper);
        favoriteFragment = new FavoriteFragment(databaseHelper);
        historyFragment = new HistoryFragment(databaseHelper);
        searchFragment = new SearchFragment();
        helpFragment = new HelpFragment();
        aboutFragment = new AboutFragment();

        // Search Fragment will be put into Fragment stack first
        goToFragment(mainFragment);
    }

    /**
     * Handle Fragments's events
     */
    private void handleFragmentsEvent() {
        mainFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Intent intent = new Intent(MainActivity.this, OnlineSearchingActivity.class);
                intent.putExtra(Constants.SEARCH_WORD, value);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onItemClick(BookmarkWord favoriteWord) {

            }
        });

        searchFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                goToActivity(value, dictionary_type);
            }

            @Override
            public void onItemClick(BookmarkWord favoriteWord) {

            }
        });

        favoriteFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
            }

            @Override
            public void onItemClick(BookmarkWord favoriteWord) {
                String word = favoriteWord.getDisplayWord();
                dictionary_type = favoriteWord.getDictionary_type();
                goToActivity(word, dictionary_type);
            }
        });

        historyFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
            }

            @Override
            public void onItemClick(BookmarkWord favoriteWord) {
                String word = favoriteWord.getDisplayWord();
                dictionary_type = favoriteWord.getDictionary_type();
                goToActivity(word, dictionary_type);
            }
        });
    }

    /**
     * Get latest state of dictionary
     * 1. Latest time when user left the app
     * 2. Type of dictionary: ENG-POR or POR-ENG
     */
    private void getLatestState() {
        String dictionary_last_type = DictionaryState.getState(this, Constants.DICTIONARY_TYPE);
        long dictionary_last_time_open = DictionaryState.getLastTimeOpen(this, Constants.LAST_TIME_OPEN);

        // Get the last dictionary type
        // else load default value
        if (dictionary_last_type != null) {
            dictionary_type = Integer.parseInt(dictionary_last_type);

            if (dictionary_type == Constants.ENG_POR) {
                ivDictionaryType.setImageResource(R.drawable.img_english_flag);
            } else if (dictionary_type == Constants.POR_ENG) {
                ivDictionaryType.setImageResource(R.drawable.img_portuguese_flag);
            }
        } else {
            dictionary_type = Constants.ENG_POR;
            ivDictionaryType.setImageResource(R.drawable.img_english_flag);
        }

        // Get last open time before user left
        if (dictionary_last_time_open != 0) {
            // Calculate the difference between current time and last time
            Calendar current_calendar = Calendar.getInstance();
            int day_difference = (int) (current_calendar.getTimeInMillis() - dictionary_last_time_open) / Constants.MILLIS_TO_DAY;

            // Compare the current date and the latest opened date
            if (day_difference >= 1) {
                loadRandomWord();
            } else {
                // Set the calendar to last_time in millis
                Calendar last_time = Calendar.getInstance();
                last_time.setTimeInMillis(dictionary_last_time_open);

                if (current_calendar.get(Calendar.DATE) - last_time.get(Calendar.DATE) == 1) {
                    loadRandomWord();
                }
            }
        }
    }

    /**
     * Get a new word each day for user
     */
    public void loadRandomWord() {
        DictionaryWord randomWord = databaseHelper.getRandomWord();

        //String displayWord = randomWord.getDisplayWord();
        //String explanation = randomWord.getExplanations();

        Bundle bundle = new Bundle();
        bundle.putSerializable("random_word", randomWord);
        //bundle.putString(Constants.WORD_OF_THE_DAY, displayWord);
        //bundle.putString(Constants.WORD_OF_THE_DAY_EXPLANATION, explanation);
        mainFragment.setArguments(bundle);
    }

    /**
     * Pass the data list from MainActivity to SearchFragment
     */
    private void passDataToSearchFragment() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.SUGGESTION_LIST, suggestionList);
        searchFragment.setArguments(bundle);
    }

    /**
     * Pop up menu will show up to let user choose dictionary type
     */
    private void showPopUpMenu() {
        ivDictionaryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, ivDictionaryType);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_dictionary_type, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        Fragment current_fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainerGeneral);

                        String current_word;

                        switch (menuItem.getItemId()) {

                            case R.id.menu_eng_por:
                                // Set dictionary type = ENG-POR
                                dictionary_type = Constants.ENG_POR;
                                ivDictionaryType.setImageResource(R.drawable.img_english_flag);

                                // Check if the search bar is already containing searching word or not
                                current_word = searchBarMain.getText();
                                if (!current_word.isEmpty()) {
                                    // Reset data source of SearchFragment
                                    if (current_fragment instanceof SearchFragment) {
                                        // Get suggestion list and pass to SearchFragment
                                        suggestionList = databaseHelper.getSuggestions(current_word, dictionary_type);
                                        passDataToSearchFragment();
                                        // Reset the list of suggested words depending on dictionary_type
                                        searchFragment.resetDataSource();
                                    }
                                }

                                // Save the current dictionary state into Shared Preferences
                                DictionaryState.saveState(
                                        MainActivity.this,
                                        Constants.DICTIONARY_TYPE,
                                        String.valueOf(dictionary_type));

                                break;

                            case R.id.menu_por_eng:
                                // Set dictionary type = POR-ENG
                                dictionary_type = Constants.POR_ENG;
                                ivDictionaryType.setImageResource(R.drawable.img_portuguese_flag);

                                // Check if the search bar is already containing searching word or not
                                current_word = searchBarMain.getText();
                                if (!current_word.isEmpty()) {
                                    // Reset data source of SearchFragment
                                    if (current_fragment instanceof SearchFragment) {
                                        // Get suggestion list and pass to SearchFragment
                                        suggestionList = databaseHelper.getSuggestions(current_word, dictionary_type);
                                        passDataToSearchFragment();
                                        // Reset the list of suggested words depending on dictionary_type
                                        searchFragment.resetDataSource();
                                    }
                                }

                                // Save the current dictionary state into Shared Preferences
                                DictionaryState.saveState(
                                        MainActivity.this,
                                        Constants.DICTIONARY_TYPE,
                                        String.valueOf(dictionary_type));

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    /**
     * Handle button event clicked
     */
    private void setButtonClicked() {
        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open navigation drawer
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        ibVoiceSearchMain.setOnClickListener(new View.OnClickListener() {
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
     * Handle navigation menu items click
     */
    private void setupNavigationDrawer() {
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.navigation_home);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int item = menuItem.getItemId();

                switch (item) {

                    // Call main fragment
                    case R.id.navigation_home:
                        goToFragment(mainFragment);
                        break;

                        // Call share intent
                    case R.id.navigation_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;

                    // Call FavoriteFragment
                    case R.id.navigation_favorite:
                        //passDataToFavoriteFragment();
                        goToFragment(favoriteFragment);
                        break;

                    // Call HistoryFragment
                    case R.id.navigation_history:
                        //passDataToHistoryFragment();
                        goToFragment(historyFragment);
                        break;

                    // Call HelpFragment
                    case R.id.navigation_help:
                        goToFragment(helpFragment);
                        break;

                    // Call AboutFragment
                    case R.id.navigation_about:
                        goToFragment(aboutFragment);
                        break;
                }

                // Close navigation menu
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        // Set translate animation to menu navigation and main layout
        animateNavigationDrawer();
    }

    /**
     * Set animation to menu navigation and main layout
     */
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - Constants.END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                linearLayoutMain.setScaleX(offsetScale);
                linearLayoutMain.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = linearLayoutMain.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                linearLayoutMain.setTranslationX(xTranslation);
            }
        });
    }

    /**
     * Handle search event during text change
     * 1. Before text change
     * 2. On text change
     * 3. After text change
     */
    private void setSearchTextChange() {
        searchBarMain.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString();

                if (!word.isEmpty()) {
                    // Get suggestion list and pass to SearchFragment
                    suggestionList = databaseHelper.getSuggestions(word, dictionary_type);
                    passDataToSearchFragment();

                    // Reset suggestion source
                    searchFragment.resetDataSource();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Handle search action event
     * 1. On search state changed
     * 2. On search confirm
     * 3. On button clicked
     */
    private void setSearchAction() {
        searchBarMain.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Set item checked to Home
                navigationView.setCheckedItem(R.id.navigation_home);

                //Fragment currFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainerGeneral);

                if (enabled) {
                    goToFragment(searchFragment);
                } else {
                    goToFragment(mainFragment);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                goToActivity(text.toString(), dictionary_type);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    /**
     * Begin transaction, switch fragments among others
     *
     * @param fragment
     */
    private void goToFragment(Fragment fragment) {
        // Remove previous fragment (if any)
        if (getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainerGeneral) != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainerGeneral))
                    .commit();
        }

        // Replace by another fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutContainerGeneral, fragment)
                .commit();
    }

    /**
     * Call intent to Detail Activity
     *
     * @param word
     */
    private void goToActivity(String word, int dictionary_type) {
        // Get id of the word
        int wordList_id = databaseHelper.getWordListId(word, dictionary_type);

        // If word exists -> go to DetailActivity
        // else go to OnlineSearchingActivity
        if (wordList_id != -1) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(Constants.WORD_LIST_ID, wordList_id);
            intent.putExtra(Constants.DICTIONARY_TYPE, dictionary_type);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Word not found, please search online", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get text from speech
        if (requestCode == Constants.REQUEST_SPEECH_RECOGNIZER && resultCode == RESULT_OK && data != null) {
            // Get text from speech
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = matches.get(0);

            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

            // Pass text to MainFragment
            Bundle bundle = new Bundle();
            bundle.putString(Constants.TEXT_TRANSLATION, text);
            mainFragment.setArguments(bundle);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // Get current date and save into shared preferences
        Calendar calendar = Calendar.getInstance();
        long current_date = calendar.getTimeInMillis();
        DictionaryState.saveLastTimeOpen(this, Constants.LAST_TIME_OPEN, current_date);

        // Close db
        databaseHelper.close();
        super.onDestroy();
    }

}