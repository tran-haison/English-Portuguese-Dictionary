package com.tranhaison.englishportugesedictionary.models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tranhaison.englishportugesedictionary.fragments.DictionaryFragment;
import com.tranhaison.englishportugesedictionary.fragments.FavoritesFragment;
import com.tranhaison.englishportugesedictionary.FragmentListener;
import com.tranhaison.englishportugesedictionary.fragments.HistoryFragment;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    // Init instances
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialSearchBar searchBar;
    ImageView imageViewLogo;
    TextView textViewPrompt;
    FrameLayout fragment_container;

    DictionaryFragment dictionaryFragment;
    FavoritesFragment favoritesFragment;
    HistoryFragment historyFragment;
    SearchFragment searchFragment;

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mapViews();
        initFragments();

        fragmentsListener();

        onNavigationItemSelected();
        onSearchTextChange();
        onSearchAction();
    }

    private void mapViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        searchBar = findViewById(R.id.search_bar);
        imageViewLogo = findViewById(R.id.imageViewLogo);
        textViewPrompt = findViewById(R.id.textViewPrompt);
        fragment_container = findViewById(R.id.fragment_container);
    }

    private void initFragments() {
        dictionaryFragment = new DictionaryFragment();
        favoritesFragment = new FavoritesFragment();
        historyFragment = new HistoryFragment();
        searchFragment = new SearchFragment();

        goToFragment(searchFragment, true);
        fragment_container.setVisibility(View.GONE);
    }

    private void setViewVisibility(boolean isFragment) {
        if (isFragment) {
            imageViewLogo.setVisibility(View.GONE);
            textViewPrompt.setVisibility(View.GONE);
            fragment_container.setVisibility(View.VISIBLE);
        } else {
            imageViewLogo.setVisibility(View.VISIBLE);
            textViewPrompt.setVisibility(View.VISIBLE);
            fragment_container.setVisibility(View.GONE);
        }
    }

    private void fragmentsListener() {
        dictionaryFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                goToFragment(favoritesFragment, false);
            }
        });

        favoritesFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                goToFragment(dictionaryFragment, true);
            }
        });

        searchFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onNavigationItemSelected() {
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
                        goToFragment(favoritesFragment, false);
                        break;
                    case R.id.navigation_history:
                        setViewVisibility(true);
                        goToFragment(historyFragment, false);
                        break;
                    case R.id.navigation_help:
                        Toast.makeText(MainActivity.this, "Help", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_about:
                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
    }

    private void onSearchTextChange() {
        searchBar.addTextChangeListener(new TextWatcher() {
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

    private void onSearchAction() {
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                Fragment currFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (enabled) {
                    if (!(currFragment instanceof  SearchFragment))
                        goToFragment(searchFragment, true);

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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    private void goToFragment(Fragment fragment, boolean isTop) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (!isTop) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

}