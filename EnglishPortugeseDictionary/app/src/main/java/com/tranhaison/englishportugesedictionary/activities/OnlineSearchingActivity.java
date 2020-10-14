package com.tranhaison.englishportugesedictionary.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.AdsManager;
import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.network.NetworkChangeReceiver;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;
import com.tranhaison.englishportugesedictionary.network.RegisterNetworkReceiver;

public class OnlineSearchingActivity extends AppCompatActivity implements NetworkChangeReceiver.ConnectivityReceiverListener {

    // Init Views and Layout
    LinearLayout linearLayoutOnlineSearching;
    ImageButton ibCloseSearching, ibRefreshWebView;
    WebView webViewSearching;
    AlertDialog internetConnectionDialog;

    // URL
    String URL;
    boolean isFinishLoaded = false;

    // Network receiver
    private RegisterNetworkReceiver registerNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_searching);

        // Map views
        mapViews();

        // Create Google Interstitial Ad
        AdsManager.createGoogleInterstitialAd(this);

        // Load URL
        setWebViewClientListener();
        URL = getSearchWordURL();
        loadURL(URL);

        // Start register network
        registerNetworkReceiver = new RegisterNetworkReceiver();
        registerNetworkReceiver.startNetworkChangeReceiver(this);

        // Return to Main Activity
        finishActivity();
    }

    /**
     * Map views from layout file
     */
    private void mapViews() {
        linearLayoutOnlineSearching = findViewById(R.id.linearLayoutOnlineSearching);
        ibCloseSearching = findViewById(R.id.ibCloseSearching);
        ibRefreshWebView = findViewById(R.id.ibRefreshWebView);
        webViewSearching = findViewById(R.id.webViewSearching);
    }

    /**
     * Get word from Detail Activity
     * if word is null, return default URL
     * otherwise return URL with search word
     *
     * @return URL
     */
    private String getSearchWordURL() {
        String search_word = getIntent().getStringExtra(Constants.SEARCH_WORD);
        if (search_word != null) {
            return "https://www.linguee.com/english-portuguese/search?source=auto&query=" + search_word;
        } else {
            return "https://www.linguee.com/english-portuguese";
        }
    }

    private void setWebViewClientListener() {
        webViewSearching.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Page loading finished
                URL = url;
                isFinishLoaded = true;

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Page loading failed
                isFinishLoaded = false;

                super.onReceivedError(view, request, error);
            }
        });
    }

    /**
     * Connect web view with URL used to search for that word
     *
     * @param URL
     */
    public void loadURL(String URL) {
        if (!NetworkUtil.isNetworkConnected(this)) {
            internetConnectionDialog = NetworkUtil.displayConnectNetworkDialog(this);
        } else {
            //webViewSearching.setWebViewClient(new WebViewClient());
            webViewSearching.loadUrl(URL);
        }
    }

    public void reloadWebView(View view) {
        webViewSearching.loadUrl(URL);
    }

    /**
     * Close activity and return to MainActivity if user click on close button
     */
    private void finishActivity() {
        ibCloseSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show interstitial ad
                AdsManager.showGoogleInterstitialAd(OnlineSearchingActivity.this);
            }
        });
    }

    @Override
    public void networkAvailable() {
        Toast.makeText(this, getString(R.string.connected), Toast.LENGTH_SHORT).show();

        if (internetConnectionDialog != null) {
            internetConnectionDialog.dismiss();
        }

        if (!isFinishLoaded) {
            loadURL(URL);
            //webViewSearching.reload();
        }
    }

    @Override
    public void networkUnavailable() {
        //Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
        Snackbar.make(linearLayoutOnlineSearching, getString(R.string.no_internet_connection), BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        registerNetworkReceiver.unregisterNetworkChangeReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerNetworkReceiver.registerNetworkChangeReceiver(this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_INTERNET_CONNECTION) {
            if (!NetworkUtil.isNetworkConnected(this)) {
                internetConnectionDialog = NetworkUtil.displayConnectNetworkDialog(this);
            } else {
                loadURL(URL);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (webViewSearching.canGoBack()) {
            webViewSearching.goBack();
        } else {
            // Show interstitial ad
            AdsManager.showGoogleInterstitialAd(OnlineSearchingActivity.this);
        }
    }
}