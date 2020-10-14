package com.tranhaison.englishportugesedictionary.activities.bookmarks;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.activities.SearchActivity;
import com.tranhaison.englishportugesedictionary.activities.bookmarks.utils.BookmarkUtils;
import com.tranhaison.englishportugesedictionary.adapters.historyactivity.HistoryAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.utils.LoadDatabase;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

public class HistoryActivity extends AppCompatActivity {

    // Init Views
    ImageButton ibBackFromHistory, ibHistoryDelete;
    ListView listViewHistory;
    LinearLayout linearLayoutHistoryEmptyPrompt;

    // Init adapter
    HistoryAdapter historyAdapter;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Map views
        mapViews();

        // Init db
        initDatabase();

        // Init list view
        initListViewHistory();

        // Set prompt's visibility
        setPromptVisibility();
    }

    /**
     * Map Views from layout resource file
     */
    private void mapViews() {
        ibBackFromHistory = findViewById(R.id.ibBackFromHistory);
        ibHistoryDelete = findViewById(R.id.ibHistoryDelete);
        listViewHistory = findViewById(R.id.listViewHistory);
        linearLayoutHistoryEmptyPrompt = findViewById(R.id.linearLayoutHistoryEmptyPrompt);
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

    private void initListViewHistory() {
        historyAdapter = new HistoryAdapter(this, databaseHelper);
        listViewHistory.setAdapter(historyAdapter);

        // List view item clicked
        historyAdapter.setOnItemListener(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                BookmarkWord historyWord = (BookmarkWord) historyAdapter.getItem(position);
                BookmarkUtils.goToDetailActivity(
                        historyWord.getWordList_id(),
                        historyWord.getDictionary_type(),
                        HistoryActivity.this
                );
            }
        });
    }

    /**
     * If Favorite is empty -> show the prompt
     * else hide it
     */
    private void setPromptVisibility() {
        if (historyAdapter.getCount() == 0) {
            linearLayoutHistoryEmptyPrompt.setVisibility(View.VISIBLE);
        } else {
            linearLayoutHistoryEmptyPrompt.setVisibility(View.GONE);
        }
    }

    public void setSearchFromHistory(View view) {
        startActivity(new Intent(HistoryActivity.this, SearchActivity.class));
        finish();
    }

    public void returnFromHistoryActivity(View view) {
        super.onBackPressed();
    }

    public void deleteHistory(View view) {
        // Clear all words in list in case list is not empty
        if (historyAdapter.getCount() != 0) {
            // Delete all in History
            createDeleteDialog();
        } else {
            Toast.makeText(this, getString(R.string.empty_list), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display dialog to ask user delete all History or not
     */
    private void createDeleteDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.CustomDialog);

        // Map views
        View view = getLayoutInflater().inflate(R.layout.alert_delete_dialogue, null);
        Button btnCancelDelete = view.findViewById(R.id.btnCancelDelete);
        Button btnConfirmDelete = view.findViewById(R.id.btnConfirmDelete);
        alert.setView(view);

        // Create alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        // Button cancel clicked
        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Button confirm clicked
        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete all in History
                databaseHelper.deleteAllHistory();
                historyAdapter.deleteAll();
                historyAdapter.notifyDataSetChanged();

                setPromptVisibility();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}