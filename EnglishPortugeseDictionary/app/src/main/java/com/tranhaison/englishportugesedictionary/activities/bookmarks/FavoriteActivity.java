package com.tranhaison.englishportugesedictionary.activities.bookmarks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.activities.SearchActivity;
import com.tranhaison.englishportugesedictionary.activities.bookmarks.utils.BookmarkUtils;
import com.tranhaison.englishportugesedictionary.adapters.favoriteactivity.FavoriteAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.utils.LoadDatabase;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

public class FavoriteActivity extends AppCompatActivity {

    // Init views
    ImageButton ibBackFromFavorite;
    ListView listViewFavorite;
    LinearLayout linearLayoutFavoriteEmptyPrompt;

    // Init global variable
    FavoriteAdapter favoriteAdapter;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Map Views
        mapViews();

        // Init db
        initDatabase();

        // Init list view
        initListViewFavorite();

        // Set prompt's visibility
        setPromptVisibility();
    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        ibBackFromFavorite = findViewById(R.id.ibBackFromFavorite);
        listViewFavorite = findViewById(R.id.listViewFavorite);
        linearLayoutFavoriteEmptyPrompt = findViewById(R.id.linearLayoutFavoriteEmptyPrompt);
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

    private void initListViewFavorite() {
        favoriteAdapter = new FavoriteAdapter(this, databaseHelper);
        listViewFavorite.setAdapter(favoriteAdapter);

        // List view item clicked
        favoriteAdapter.setOnItemClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                BookmarkWord favoriteWord = (BookmarkWord) favoriteAdapter.getItem(position);

                BookmarkUtils.goToDetailActivity(
                        favoriteWord.getWordList_id(),
                        favoriteWord.getDictionary_type(),
                        FavoriteActivity.this
                );
            }
        });

        // List view item delete clicked
        favoriteAdapter.setOnItemDeleteClick(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                // Get word
                BookmarkWord favoriteWord = (BookmarkWord) favoriteAdapter.getItem(position);

                // Delete word from Favorite
                databaseHelper.deleteFavorite(favoriteWord.getWordList_id());
                favoriteAdapter.removeWord(position);
                favoriteAdapter.notifyDataSetChanged();

                // Set prompt to visible if Favorite is empty
                setPromptVisibility();

                // Inform user
                String word = favoriteWord.getDisplayWord();
                Toast.makeText(FavoriteActivity.this, word + getString(R.string.is_removed_from_favorite), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * If Favorite is empty -> show the prompt
     * else hide it
     */
    private void setPromptVisibility() {
        if (favoriteAdapter.getCount() == 0) {
            linearLayoutFavoriteEmptyPrompt.setVisibility(View.VISIBLE);
        } else {
            linearLayoutFavoriteEmptyPrompt.setVisibility(View.GONE);
        }
    }

    /**
     * Search for a word if Favorite is empty
     * @param view
     */
    public void setSearchFromFavorite(View view) {
        startActivity(new Intent(FavoriteActivity.this, SearchActivity.class));
        finish();
    }

    public void returnFromFavoriteActivity (View view) {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}