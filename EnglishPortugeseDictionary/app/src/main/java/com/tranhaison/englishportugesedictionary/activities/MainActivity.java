package com.tranhaison.englishportugesedictionary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.DictionaryState;
import com.tranhaison.englishportugesedictionary.fragments.FavoritesFragment;
import com.tranhaison.englishportugesedictionary.interfaces.FragmentListener;
import com.tranhaison.englishportugesedictionary.fragments.HistoryFragment;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    // Init Views
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialSearchBar searchBar;
    ImageView ivLogo;
    TextView tvPrompt;
    Button btnDictionaryType;
    FrameLayout frameLayoutContainer;

    // Init Fragments
    FavoritesFragment favoritesFragment;
    HistoryFragment historyFragment;
    SearchFragment searchFragment;

    // Init a variable to get current dictionary type (default = ENG - POR)
    public int dictionary_type = Constants.ENG_POR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Map Views from layout file
        mapViews();

        // Get the latest dictionary state
        getLatestState();

        // Init and handle Fragments's event
        initFragments();
        handleFragmentsEvent();

        // Handle events for Views and Menu
        setShowPopUpMenu();
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
        frameLayoutContainer = findViewById(R.id.frameLayoutContainer);
    }

    /**
     * Get instances of Fragments
     * Put 1st Fragment into stack
     * Set visibility to Fragment and Views
     */
    private void initFragments() {
        favoritesFragment = new FavoritesFragment();
        historyFragment = new HistoryFragment();
        searchFragment = new SearchFragment();

        // Search Fragment will be put into Fragment stack first
        goToFragment(searchFragment);

        // Set visibility to Fragment and Views
        frameLayoutContainer.setVisibility(View.GONE);
    }

    /**
     * Get latest dictionary state before exiting
     */
    private void getLatestState() {
        String value = DictionaryState.getState(this, Constants.DICTIONARY_TYPE);

        if (value != null) {
            dictionary_type = Integer.parseInt(value);

            if (dictionary_type == Constants.ENG_POR) {
                btnDictionaryType.setText("E-P");
            } else if (dictionary_type == Constants.POR_ENG) {
                btnDictionaryType.setText("P-E");
            }
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
            frameLayoutContainer.setVisibility(View.VISIBLE);
        } else {
            ivLogo.setVisibility(View.VISIBLE);
            tvPrompt.setVisibility(View.VISIBLE);
            frameLayoutContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Pop up menu will show up to let user choose dictionary type
     */
    private void setShowPopUpMenu() {
        btnDictionaryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnDictionaryType);
                popupMenu.getMenuInflater().inflate(R.menu.menu_dictionary_type, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_eng_por:
                                // Set dictionary type = ENG-POR
                                dictionary_type = Constants.ENG_POR;
                                btnDictionaryType.setText("E-P");

                                // Save the current dictionary type into Shared Preferences
                                DictionaryState.saveState(MainActivity.this, Constants.DICTIONARY_TYPE, String.valueOf(dictionary_type));

                                break;
                            case R.id.menu_por_eng:
                                // Set dictionary type = POR-ENG
                                dictionary_type = Constants.POR_ENG;
                                btnDictionaryType.setText("P-E");

                                // Save the current dictionary type into Shared Preferences
                                DictionaryState.saveState(MainActivity.this, Constants.DICTIONARY_TYPE, String.valueOf(dictionary_type));

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
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        });

        favoritesFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        });

        historyFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        });
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
                    case R.id.navigation_setting:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_favorite:
                        setViewVisibility(true);
                        goToFragment(favoritesFragment);
                        break;
                    case R.id.navigation_history:
                        setViewVisibility(true);
                        goToFragment(historyFragment);
                        break;
                    case R.id.navigation_help:
                        Toast.makeText(MainActivity.this, "Help", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_about:
                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
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
                searchFragment.filterValue(charSequence.toString());
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
                Fragment currFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainer);

                // If search is enabled -> switch current fragment to SearchFragment and display SearchFragment
                // else display app's logo and prompt
                if (enabled) {
                    if (!(currFragment instanceof  SearchFragment)) {
                        goToFragment(searchFragment);
                    }
                    setViewVisibility(true);
                } else {
                    setViewVisibility(false);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        drawerLayout.openDrawer(GravityCompat.START);
                        navigationView.bringToFront();
                        break;
                    case MaterialSearchBar.BUTTON_SPEECH:
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
        // Remove previous fragment
        if (getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainer) != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainer))
                    .commit();
        }

        // Replace by another fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutContainer, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}