package com.tranhaison.englishportugesedictionary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.tranhaison.englishportugesedictionary.R;

public class SettingActivity extends AppCompatActivity {

    // Init Views
    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        // Map Views from layout
        mapViews();

        // Handle events
        returnToPreviousActivity();
    }

    /**
     * Map views from layout file
     */
    private void mapViews() {
        ibBack = findViewById(R.id.ibBack);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}