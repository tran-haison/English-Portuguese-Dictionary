package com.tranhaison.englishportugesedictionary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;

public class OnlineSearchingActivity extends AppCompatActivity {

    // Init Views and Layout
    ImageButton ibCloseSearching;
    WebView webViewSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_online_searching);

        // Map views
        mapViews();

        // Get a word that need searching
        String word = getSearchWord();

        // Open website
        String URL = "https://www.linguee.com/english-portuguese/search?source=auto&query=" + word;
        openURL(URL);

        // Return to Main Activity
        closeSearchingActivity();
    }

    /**
     * Map views from layout file
     */
    private void mapViews() {
        ibCloseSearching = findViewById(R.id.ibCloseSearching);
        webViewSearching = findViewById(R.id.webViewSearching);
    }

    /**
     * Get a word from other activities
     */
    private String getSearchWord() {
        Intent intent = getIntent();

        String search_word = intent.getStringExtra(Constants.SEARCH_WORD);
        if (search_word != null) {
            return search_word;
        }

        return null;
    }

    /**
     * Connect web view with URL used to search for that word
     * @param URL
     */
    public void openURL(String URL) {
        webViewSearching.setWebViewClient(new WebViewClient());
        webViewSearching.loadUrl(URL);
    }

    /**
     * Close activity and return to MainActivity if user click on close button
     */
    private void closeSearchingActivity() {
        ibCloseSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToMainActivity();
            }
        });
    }

    /**
     * Return to Main Activity after searching online for a word
     */
    private void returnToMainActivity() {
        Intent intent = new Intent(OnlineSearchingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (webViewSearching.canGoBack()) {
            webViewSearching.goBack();
        } else {
            returnToMainActivity();
        }
    }
}