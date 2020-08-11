package com.tranhaison.englishportugesedictionary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tranhaison.englishportugesedictionary.DictionaryWord;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.LoadDatabase;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.AboutFragment;
import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.DictionaryState;
import com.tranhaison.englishportugesedictionary.DictionaryDataType;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.HelpFragment;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.FavoriteFragment;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.HistoryFragment;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.fragments.mainactivity.SearchFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Init Views
    DrawerLayout drawerLayout;
    FrameLayout frameLayoutContainerGeneral;
    NavigationView navigationView;
    MaterialSearchBar searchBar;
    ImageView ivLogo;
    TextView tvPrompt;
    Button btnDictionaryType;

    // Init Fragments
    FavoriteFragment favoriteFragment;
    HistoryFragment historyFragment;
    SearchFragment searchFragment;
    HelpFragment helpFragment;
    AboutFragment aboutFragment;

    // Init Database Helper and Adapter
    DatabaseHelper databaseHelper;
    DictionaryDataType dictionaryDataType;

    // Init variables to hold current dictionary type (default = ENG - POR)
    private int dictionary_type = Constants.ENG_POR;
    private ArrayList<DictionaryWord> suggestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Map Views from layout file
        mapViews();

        // Init Database
        initDatabase();

        // Get the latest dictionary state
        getLatestState();

        // Init and handle Fragments's event
        initFragments();
        handleFragmentsEvent();

        // Pass data list to Search Fragment when user first open the app
        //passDataToSearchFragment();

        // Handle events for Views and Menu
        showPopUpMenu();
        setNavigationItemSelected();
        setSearchTextChange();
        setSearchAction();
    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        searchBar = findViewById(R.id.search_bar);
        ivLogo = findViewById(R.id.ivLogo);
        tvPrompt = findViewById(R.id.tvPrompt);
        btnDictionaryType = findViewById(R.id.btnDictionaryType);
        frameLayoutContainerGeneral = findViewById(R.id.frameLayoutContainerGeneral);
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
        favoriteFragment = new FavoriteFragment(databaseHelper);
        historyFragment = new HistoryFragment(databaseHelper);
        searchFragment = new SearchFragment();
        helpFragment = new HelpFragment();
        aboutFragment = new AboutFragment();

        // Search Fragment will be put into Fragment stack first
        goToFragment(searchFragment);

        // Set visibility to Fragment and Views
        frameLayoutContainerGeneral.setVisibility(View.GONE);
    }

    /**
     * Get latest dictionary's state before exiting
     */
    private void getLatestState() {
        String value = DictionaryState.getState(this, Constants.DICTIONARY_TYPE);
        dictionaryDataType = new DictionaryDataType(databaseHelper);

        // Get the latest state of dictionary type and load correspond data
        if (value != null) {
            dictionary_type = Integer.parseInt(value);

            // Set text to btnDictionaryType and load data depend on dictionary_type
            if (dictionary_type == Constants.ENG_POR) {
                btnDictionaryType.setText(R.string.e_p);
                //dataList = dictionaryDataType.getEngPor();
            } else if (dictionary_type == Constants.POR_ENG) {
                btnDictionaryType.setText(R.string.p_e);
                //dataList = dictionaryDataType.getPorEng();
            }
        } else {
            // Default dictionary type
            dictionary_type = Constants.ENG_POR;
            btnDictionaryType.setText(R.string.e_p);
            //dataList = dictionaryDataType.getEngPor();
        }
    }

    /**
     * Set Visibility to ivLogo, tvPrompt and flContainer
     * @param isFragment
     */
    private void setViewVisibility(boolean isFragment) {
        if (isFragment) {
            ivLogo.setVisibility(View.GONE);
            tvPrompt.setVisibility(View.GONE);
            frameLayoutContainerGeneral.setVisibility(View.VISIBLE);
        } else {
            ivLogo.setVisibility(View.VISIBLE);
            tvPrompt.setVisibility(View.VISIBLE);
            frameLayoutContainerGeneral.setVisibility(View.GONE);
        }
    }

    /**
     * Pop up menu will show up to let user choose dictionary type
     */
    private void showPopUpMenu() {
        btnDictionaryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnDictionaryType);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_dictionary_type, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Fragment current_fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainerGeneral);

                        switch (menuItem.getItemId()) {
                            case R.id.menu_eng_por:
                                // Set dictionary type = ENG-POR
                                dictionary_type = Constants.ENG_POR;
                                btnDictionaryType.setText(R.string.e_p);

                                // Get English - Portuguese data source
                                //dataList = dictionaryDataType.getEngPor();
                                //passDataToSearchFragment();

                                // Reset data source of SearchFragment
                                if (current_fragment instanceof SearchFragment) {
                                    searchFragment.resetDataSource();
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
                                btnDictionaryType.setText(R.string.p_e);

                                // Get Portuguese - English data source
                                //dataList = dictionaryDataType.getPorEng();
                                //passDataToSearchFragment();

                                // Reset data source of SearchFragment
                                if (current_fragment instanceof SearchFragment) {
                                    searchFragment.resetDataSource();
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
     * Handle Fragments's events
     */
    private void handleFragmentsEvent() {
        searchFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                goToDetailActivity(value);
            }
        });

        favoriteFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                goToDetailActivity(value);
            }
        });

        historyFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                goToDetailActivity(value);
            }
        });
    }

    /**
     * Pass the data list from MainActivity to SearchFragment
     */
    private void passDataToSearchFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("suggestion_list", suggestionList);
        searchFragment.setArguments(bundle);
    }

    /**
     * Get the list of History and pass to HistoryFragment
     */
    private void passDataToHistoryFragment() {
        ArrayList<DictionaryWord> historyList = databaseHelper.getAllHistory();
        Bundle bundle = new Bundle();
        bundle.putSerializable("history_list", historyList);
        historyFragment.setArguments(bundle);
    }

    /**
     * Get a list of Favorite and pass to FavoriteFragment
     */
    private void passDataToFavoriteFragment() {
        ArrayList<DictionaryWord> favoriteList = databaseHelper.getAllFavorite();
        Bundle bundle = new Bundle();
        bundle.putSerializable("favorite_list", favoriteList);
        favoriteFragment.setArguments(bundle);
    }

    /**
     * Handle navigation menu items click
     */
    private void setNavigationItemSelected() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int item = menuItem.getItemId();

                switch (item) {

                    // Call intent to SettingActivity
                    case R.id.navigation_setting:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                        break;

                    // Call FavoriteFragment
                    case R.id.navigation_favorite:
                        setViewVisibility(true);
                        passDataToFavoriteFragment();
                        goToFragment(favoriteFragment);
                        break;

                    // Call HistoryFragment
                    case R.id.navigation_history:
                        setViewVisibility(true);
                        passDataToHistoryFragment();
                        goToFragment(historyFragment);
                        break;

                    // Call HelpFragment
                    case R.id.navigation_help:
                        setViewVisibility(true);
                        goToFragment(helpFragment);
                        break;

                    // Call AboutFragment
                    case R.id.navigation_about:
                        setViewVisibility(true);
                        goToFragment(aboutFragment);
                        break;
                }

                // Close navigation menu
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
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
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString();

                if (word.isEmpty()) {
                    setViewVisibility(false);
                } else {
                    // Get suggestion list and pass to SearchFragment
                    suggestionList = databaseHelper.getSuggestions(word);
                    passDataToSearchFragment();

                    // Reset suggestion source
                    searchFragment.resetDataSource();

                    setViewVisibility(true);
                }
                //searchFragment.filterSearch(charSequence.toString());
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
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Get current fragment
                Fragment currFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainerGeneral);

                // If search is enabled -> switch current fragment to SearchFragment and display SearchFragment
                // else display app's logo and prompt
                if (enabled) {
                    if (!(currFragment instanceof SearchFragment)) {
                        goToFragment(searchFragment);
                    }
                    setViewVisibility(true);
                } else {
                    setViewVisibility(false);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                String search_word = text.toString();
                goToDetailActivity(search_word);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {

                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        // Open navigation drawer
                        drawerLayout.openDrawer(GravityCompat.START);
                        navigationView.bringToFront();
                        break;

                    case MaterialSearchBar.BUTTON_SPEECH:
                        // Call speech intent
                        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_recognizer);
                        startActivityForResult(speechIntent, Constants.REQUEST_SPEECH_RECOGNIZER);
                        break;
                }
            }
        });
    }

    /**
     * Begin transaction, switch fragments among others
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
     * @param word
     */
    private void goToDetailActivity(String word) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get text from speech
        if (requestCode == Constants.REQUEST_SPEECH_RECOGNIZER && resultCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchBar.setText(matches.get(0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }


}