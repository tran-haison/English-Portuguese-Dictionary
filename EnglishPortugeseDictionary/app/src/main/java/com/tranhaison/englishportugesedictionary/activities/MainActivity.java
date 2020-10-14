package com.tranhaison.englishportugesedictionary.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.NativeAdLayout;
import com.tranhaison.englishportugesedictionary.utils.AdsManager;
import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.utils.SharedPreferencesDictionary;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.activities.bookmarks.FavoriteActivity;
import com.tranhaison.englishportugesedictionary.activities.bookmarks.HistoryActivity;
import com.tranhaison.englishportugesedictionary.activities.bookmarks.utils.BookmarkUtils;
import com.tranhaison.englishportugesedictionary.adapters.mainactivity.FavoriteFeatureAdapter;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.databases.utils.LoadDatabase;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Views and Layouts
    ImageButton ibVoiceSearchMain;
    ImageView ivRefresh, ivSearchIcon, ivTranslateIcon, ivHistoryIcon, ivFavoriteIcon;
    EditText etSearchMain;
    TextView tvSeeAll, tvCurrentDate, tvRandomWord, tvRandomWordDefinition, tvRateUs, tvShare;
    LinearLayout linearLayoutWordLookUp, linearLayoutTextTranslation, linearLayoutHistory, linearLayoutFavorite;
    RecyclerView recyclerViewFavorite;
    NativeAdLayout nativeAdLayoutMain;
    CardView ggUnifiedAdContainerMain;

    // Adapter
    FavoriteFeatureAdapter favoriteFeatureAdapter;
    ArrayList<BookmarkWord> favoriteFeatureList;

    // DatabaseHelper instance
    DatabaseHelper databaseHelper;

    // Dictionary random word
    DictionaryWord randomWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Map Views from layout file
        mapViews();

        // Create fb native banner ad
        AdsManager.createFacebookNativeAd(this, nativeAdLayoutMain, ggUnifiedAdContainerMain);

        // Init Database
        initDatabase();

        // Get the latest dictionary state
        getLatestState();

        // Init recycler view favorite
        initFavoriteFeatureRecyclerView();
    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        ivRefresh = findViewById(R.id.ivRefresh);
        ivSearchIcon = findViewById(R.id.ivSearchIcon);
        ivTranslateIcon = findViewById(R.id.ivTranslateIcon);
        ivHistoryIcon = findViewById(R.id.ivHistoryIcon);
        ivFavoriteIcon = findViewById(R.id.ivFavoriteIcon);
        tvSeeAll = findViewById(R.id.tvSeeAll);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvRandomWord = findViewById(R.id.tvRandomWord);
        tvRandomWordDefinition = findViewById(R.id.tvRandomWordDefinition);
        tvRateUs = findViewById(R.id.tvRateUs);
        tvShare = findViewById(R.id.tvShare);
        ibVoiceSearchMain = findViewById(R.id.ibVoiceSearchMain);
        etSearchMain = findViewById(R.id.etSearchMain);
        linearLayoutFavorite = findViewById(R.id.linearLayoutFavorite);
        linearLayoutHistory = findViewById(R.id.linearLayoutHistory);
        linearLayoutTextTranslation = findViewById(R.id.linearLayoutTextTranslation);
        linearLayoutWordLookUp = findViewById(R.id.linearLayoutWordLookUp);
        recyclerViewFavorite = findViewById(R.id.recyclerViewFavorite);
        nativeAdLayoutMain = findViewById(R.id.nativeAdLayoutMain);
        ggUnifiedAdContainerMain = findViewById(R.id.ggUnifiedAdContainerMain);
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

    public void setSearchViewClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(etSearchMain, "search_activity");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivityForResult(intent, Constants.REQUEST_UPDATE_FAVORITE, options.toBundle());
        } else {
            startActivityForResult(intent, Constants.REQUEST_UPDATE_FAVORITE);
        }
    }

    /**
     * Handle voice button clicked event
     */
    public void speechToText(View view) {
        // Call speech intent
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString());
        //speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, Constants.ENGLISH_SPEECH_RECOGNIZER_PROMPT);
        startActivityForResult(speechIntent, Constants.REQUEST_SPEECH_RECOGNIZER);
    }

    /**
     * Get latest state of dictionary
     * 1. Latest time when user left the app
     * 2. Type of dictionary: ENG-POR or POR-ENG
     */
    private void getLatestState() {
        // Get current date and set to text view
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvCurrentDate.setText(simpleDateFormat.format(new Date()));

        // Get date from last time and compare to current date
        boolean wasOpened = SharedPreferencesDictionary.getLastTimeOpen(this, Constants.LAST_TIME_OPEN);
        if (wasOpened) {
            getRandomWord();
        }
    }

    /**
     * Get a new random word
     * then save to preferences
     */
    public void getRandomWord() {
        // Get random word from database
        randomWord = databaseHelper.getRandomWord();
        String word = randomWord.getDisplayWord();
        String wordDefinition = randomWord.getExplanations();

        // Set text to text view
        tvRandomWord.setText(word);
        tvRandomWordDefinition.setText(wordDefinition);

        // Save the new word to shared preferences
        //SharedPreferencesDictionary.saveWord(MainActivity.this, Constants.WORD_OF_THE_DAY, word);
        //SharedPreferencesDictionary.saveWord(MainActivity.this, Constants.WORD_OF_THE_DAY_DEFINITION, wordDefinition);

        // Remove old preferences before saving new ones
        //SharedPreferencesDictionary.remove(this, Constants.WORD_OF_THE_DAY);
        //SharedPreferencesDictionary.remove(this, Constants.WORD_OF_THE_DAY_DEFINITION);
    }

    public void viewRandomWordDetail(View view) {
        String word = tvRandomWord.getText().toString();

        // Get id of word of the day
        int wordList_id = databaseHelper.getWordListId(word, Constants.ENG_POR);

        if (wordList_id != -1) {
            BookmarkUtils.goToDetailActivity(wordList_id, Constants.ENG_POR, MainActivity.this);
        }
    }

    public void refreshRandomWord(View view) {
        getRandomWord();
    }

    public void goToOnlineSearchingActivity(View view) {
        Intent intent = new Intent(MainActivity.this, OnlineSearchingActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(ivSearchIcon, "online_searching_activity");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void goToTextTranslationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, TextTranslationActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(ivTranslateIcon, "text_translation_activity");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void goToHistoryActivity(View view) {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(ivHistoryIcon, "history_activity");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivityForResult(intent, Constants.REQUEST_UPDATE_FAVORITE, options.toBundle());
        } else {
            startActivityForResult(intent, Constants.REQUEST_UPDATE_FAVORITE);
        }
    }

    public void goToFavoriteActivity(View view) {
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);

        Pair[] pairs = new Pair[1];
        if (view instanceof LinearLayout) {
            pairs[0] = new Pair<View, String>(ivFavoriteIcon, "favorite_activity");
        } else if (view instanceof TextView) {
            pairs[0] = new Pair<View, String>(tvSeeAll, "favorite_activity");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivityForResult(intent, Constants.REQUEST_UPDATE_FAVORITE, options.toBundle());
        } else {
            startActivityForResult(intent, Constants.REQUEST_UPDATE_FAVORITE);
        }
    }

    private void initFavoriteFeatureRecyclerView() {
        // Setup recycler view
        recyclerViewFavorite.setHasFixedSize(true);
        recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        try {
            favoriteFeatureList = databaseHelper.getAllFavorite();
        } catch (Exception e) {}

        if (favoriteFeatureList == null) {
            favoriteFeatureList = new ArrayList<>();
        }
        favoriteFeatureAdapter = new FavoriteFeatureAdapter(favoriteFeatureList);
        recyclerViewFavorite.setAdapter(favoriteFeatureAdapter);

        // Item clicked event
        favoriteFeatureAdapter.setRecyclerViewItemListener(new ListItemListener() {
            @Override
            public void onItemClick(int position) {
                BookmarkWord favoriteWord = favoriteFeatureList.get(position);
                BookmarkUtils.goToDetailActivity(
                        favoriteWord.getWordList_id(),
                        favoriteWord.getDictionary_type(),
                        MainActivity.this);
            }
        });
    }

    public void rateApp(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Constants.APP_ID)));
    }

    public void shareApp(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + Constants.APP_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get text from speech
        if (requestCode == Constants.REQUEST_SPEECH_RECOGNIZER && resultCode == RESULT_OK && data != null) {
            // Get text from speech
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = matches.get(0);

            // Get id of the text
            int wordList_id = databaseHelper.getWordListId(text, Constants.ENG_POR);

            // If text exists -> go to DetailActivity
            // else go to TextTranslationActivity
            if (wordList_id != -1) {
                BookmarkUtils.goToDetailActivity(wordList_id, Constants.ENG_POR, MainActivity.this);
            } else {
                Intent intent = new Intent(MainActivity.this, TextTranslationActivity.class);
                intent.putExtra(Constants.TEXT_TRANSLATION, text);
                startActivity(intent);
            }
        } else if (requestCode == Constants.REQUEST_UPDATE_FAVORITE) {
            favoriteFeatureList = databaseHelper.getAllFavorite();
            favoriteFeatureAdapter.resetFavoriteFeatureList(favoriteFeatureList);
            favoriteFeatureAdapter.notifyDataSetChanged();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        // Save the current state before user leaves the app
        SharedPreferencesDictionary.saveLastTimeOpen(this, Constants.LAST_TIME_OPEN, true);

        // Close db
        databaseHelper.close();

        super.onDestroy();
    }

}