package com.tranhaison.englishportugesedictionary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.Constants;
import com.tranhaison.englishportugesedictionary.R;

public class SplashScreenActivity extends AppCompatActivity {

    // Init Animations and Views
    Animation topAnim, bottomAnim;
    ImageView imageViewAppLogo;
    TextView textViewAppName, textViewSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        // Load Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Mapping Views
        imageViewAppLogo = findViewById(R.id.imageViewAppLogo);
        textViewAppName = findViewById(R.id.textViewAppName);
        textViewSlogan = findViewById(R.id.textViewSlogan);

        // Set animation to Views
        imageViewAppLogo.setAnimation(topAnim);
        textViewAppName.setAnimation(bottomAnim);
        textViewSlogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(imageViewAppLogo, "app_logo");
                pairs[1] = new Pair<View, String>(textViewAppName, "app_name");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }

                finish();

            }
        }, Constants.SPLASH_SCREEN_TIMER);
    }
}