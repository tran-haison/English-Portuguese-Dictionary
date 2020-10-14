package com.tranhaison.englishportugesedictionary.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.network.NetworkChangeReceiver;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;
import com.tranhaison.englishportugesedictionary.network.RegisterNetworkReceiver;
import com.tranhaison.englishportugesedictionary.utils.AdsManager;
import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.utils.SharedPreferencesDictionary;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.GoogleTextToSpeech;
import com.tranhaison.englishportugesedictionary.utils.texttospeech.LocalTextToSpeech;

public class TextTranslationActivity extends AppCompatActivity implements NetworkChangeReceiver.ConnectivityReceiverListener {

    // Init Views and Layouts
    ConstraintLayout constraintLayoutTextTranslation;
    ImageButton ibBackTextTranslation, ibTranslate;
    EditText etInputText;
    ImageView ivFlagInput, ivFlagOutput, ivSwap,
            ivClearInputText, ivSpeakerInputText, ivCopyInputText,
            ivSpeakerTranslatedText, ivCopyTranslatedText;
    TextView tvOutputText;
    Button btnTranslate;

    // Init model instances
    LocalTextToSpeech localTextToSpeech;
    GoogleTextToSpeech googleTextToSpeech;
    Translator translatorEngPor, translatorPorEng;
    AlertDialog waitingDialog, internetConnectionDialog;

    // Network receiver
    private RegisterNetworkReceiver registerNetworkReceiver;

    // Global variable to store value of input text
    private int dictionary_type = Constants.ENG_POR;
    private boolean isModelDownloaded;
    private boolean isModelDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_translation);

        // Map views
        mapViews();

        // Create Google Interstitial Ad
        AdsManager.createGoogleInterstitialAd(this);

        // Download model if needed
        // If models already exist -> get them from device
        isModelDownloaded = SharedPreferencesDictionary.getModelDownloadedState(this, Constants.MODEL_DOWNLOADED);
        downloadModel();

        // Register broadcast network for downloading model for the first time
        if (!isModelDownloaded) {
            registerNetworkReceiver = new RegisterNetworkReceiver();
            registerNetworkReceiver.startNetworkChangeReceiver(this);
        }

        // Get text
        getTextFromActivity();

        // Handle events
        copyText();
        initTextToSpeech();
        swapTextTranslationType();
        returnToPreviousActivity();

        // Clear input text
        ivClearInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etInputText.getText().toString().isEmpty()) {
                    Toast.makeText(TextTranslationActivity.this, getString(R.string.text_is_empty), Toast.LENGTH_SHORT).show();
                } else {
                    etInputText.setText("");
                }
            }
        });

    }

    /**
     * Map Views from layout file
     */
    private void mapViews() {
        constraintLayoutTextTranslation = findViewById(R.id.constraintLayoutTextTranslation);
        ibBackTextTranslation = findViewById(R.id.ibBackTextTranslation);
        ibTranslate = findViewById(R.id.ibTranslate);
        etInputText = findViewById(R.id.etInputText);
        ivFlagInput = findViewById(R.id.ivFlagInput);
        ivFlagOutput = findViewById(R.id.ivFlagOutput);
        ivSwap = findViewById(R.id.ivSwap);
        ivClearInputText = findViewById(R.id.ivClearInputText);
        ivSpeakerInputText = findViewById(R.id.ivSpeakerInputText);
        ivCopyInputText = findViewById(R.id.ivCopyInputText);
        ivSpeakerTranslatedText = findViewById(R.id.ivSpeakerTranslatedText);
        ivCopyTranslatedText = findViewById(R.id.ivCopyTranslatedText);
        tvOutputText = findViewById(R.id.tvOutputText);
        btnTranslate = findViewById(R.id.btnTranslate);
    }

    /**
     * Get text from MainActivity when user speaks a sentence
     */
    private void getTextFromActivity() {
        String text = getIntent().getStringExtra(Constants.TEXT_TRANSLATION);
        if (text != null) {
            etInputText.setText(text);
        }
    }

    /**
     * Copy text to clipboard when user click on copy button
     */
    private void copyText() {
        ivCopyTranslatedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = tvOutputText.getText().toString();
                if (!text.isEmpty()) {
                    copiedToClipboard(text);
                } else {
                    Toast.makeText(TextTranslationActivity.this, getString(R.string.text_is_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivCopyInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etInputText.getText().toString();
                if (!text.isEmpty()) {
                    copiedToClipboard(text);
                }
            }
        });
    }

    /**
     * Copied text to clipboard
     *
     * @param text
     */
    private void copiedToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(Constants.CLIPBOARD_LABEL, text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Initialize text to speech instance
     */
    private void initTextToSpeech() {
        // Text to speech online with Google voice
        googleTextToSpeech = new GoogleTextToSpeech(TextTranslationActivity.this);

        // Local text to speech
        localTextToSpeech = new LocalTextToSpeech(this);
        localTextToSpeech.initialize();
    }

    public void speakInputText(View view) {
        String text = etInputText.getText().toString();
        if (!text.isEmpty()) {
            if (dictionary_type == Constants.ENG_POR) {
                if (NetworkUtil.isNetworkConnected(TextTranslationActivity.this)) {
                    googleTextToSpeech.play(text, Constants.CODE_ENGLISH);
                } else {
                    localTextToSpeech.speakEnglish(text, Constants.CODE_US);
                }
            } else if (dictionary_type == Constants.POR_ENG) {
                if (NetworkUtil.isNetworkConnected(TextTranslationActivity.this)) {
                    googleTextToSpeech.play(text, Constants.CODE_PORTUGUESE);
                } else {
                    localTextToSpeech.speakPortuguese(text);
                }
            }
        } else {
            Toast.makeText(TextTranslationActivity.this, getString(R.string.please_enter_text_to_be_translated), Toast.LENGTH_SHORT).show();
        }
    }

    public void speakOutputText(View view) {
        String text = tvOutputText.getText().toString();
        if (!text.isEmpty()) {
            if (dictionary_type == Constants.ENG_POR) {
                if (NetworkUtil.isNetworkConnected(TextTranslationActivity.this)) {
                    googleTextToSpeech.play(text, Constants.CODE_PORTUGUESE);
                } else {
                    localTextToSpeech.speakPortuguese(text);
                }
            } else if (dictionary_type == Constants.POR_ENG) {
                if (NetworkUtil.isNetworkConnected(TextTranslationActivity.this)) {
                    googleTextToSpeech.play(text, Constants.CODE_ENGLISH);
                } else {
                    localTextToSpeech.speakEnglish(text, Constants.CODE_US);
                }
            }
        } else {
            Toast.makeText(TextTranslationActivity.this, getString(R.string.text_is_empty), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Swap text input and output type between English and Portuguese
     */
    private void swapTextTranslationType() {
        ivSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotate_anim = AnimationUtils.loadAnimation(TextTranslationActivity.this, R.anim.rotate_animation);
                ivSwap.setAnimation(rotate_anim);

                if (dictionary_type == Constants.ENG_POR) {
                    dictionary_type = Constants.POR_ENG;
                    etInputText.setHint(getString(R.string.portuguese_text));

                    changeImageViewAnimation(ivFlagInput, R.drawable.img_portugal_flag, R.anim.slide_right_animation);
                    changeImageViewAnimation(ivFlagOutput, R.drawable.img_england_flag, R.anim.slide_left_animation);
                } else {
                    dictionary_type = Constants.ENG_POR;
                    etInputText.setHint(getString(R.string.english_text));

                    changeImageViewAnimation(ivFlagInput, R.drawable.img_england_flag, R.anim.slide_right_animation);
                    changeImageViewAnimation(ivFlagOutput, R.drawable.img_portugal_flag, R.anim.slide_left_animation);
                }
            }
        });
    }

    /**
     * Set animation to image view when swapping between text input and output
     *
     * @param imageView
     * @param image_resource
     * @param anim
     */
    public void changeImageViewAnimation(final ImageView imageView, final int image_resource, int anim) {
        final Animation anim_out = AnimationUtils.loadAnimation(TextTranslationActivity.this, anim);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(image_resource);
            }
        });
        imageView.startAnimation(anim_out);
    }

    public void performTranslate(View view) {
        String text = etInputText.getText().toString();
        if (!text.isEmpty()) {
            tvOutputText.setHint(getString(R.string.translating_));
            if (dictionary_type == Constants.ENG_POR) {
                translateText(text, translatorEngPor);
            } else if (dictionary_type == Constants.POR_ENG) {
                translateText(text, translatorPorEng);
            }
        }
    }

    /**
     * Translate text
     *
     * @param text
     * @param translator
     */
    private void translateText(final String text, final Translator translator) {
        translator.translate(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(@NonNull String translatedText) {
                        // Translation successful.
                        tvOutputText.setText(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error.
                        Toast.makeText(TextTranslationActivity.this, getString(R.string.error_translation), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Get translation models which are already on device
     */
    private void getModelClient() {
        // Get options
        TranslatorOptions eng_por_options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.PORTUGUESE)
                .build();

        TranslatorOptions por_eng_options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.PORTUGUESE)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();

        // Get clients
        translatorEngPor = Translation.getClient(eng_por_options);
        translatorPorEng = Translation.getClient(por_eng_options);
    }

    /**
     * Download translator model if needed and translate text depending on input text
     */
    public void downloadModel() {
        // Get models on device if they already downloaded
        if (isModelDownloaded) {
            getModelClient();
            return;
        }

        // Download models for the first time
        if (!NetworkUtil.isWifiConnected(this)) {
            internetConnectionDialog = NetworkUtil.displayConnectNetworkDialog(this);
        } else {
            displayDialog();

            // Display snack bar to inform user
            final Snackbar snackBarDownloading = Snackbar.make(constraintLayoutTextTranslation,
                    getString(R.string.downloading_translation_model),
                    BaseTransientBottomBar.LENGTH_INDEFINITE);
            snackBarDownloading.show();

            getModelClient();
            // Downloaded model conditions (required Wifi because of its size)
            final DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            isModelDownloading = true;

            // Download english-portuguese model
            translatorEngPor.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {
                            // If eng-por has been downloaded successfully -> continue downloading por-eng model
                            // Download portuguese english model
                            translatorPorEng.downloadModelIfNeeded(conditions)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void v) {
                                            // Model downloaded successfully. Okay to start translating.

                                            // Dismiss dialog
                                            waitingDialog.dismiss();
                                            snackBarDownloading.dismiss();
                                            Toast.makeText(TextTranslationActivity.this, getString(R.string.translation_model_has_been_downloaded_successfully), Toast.LENGTH_SHORT).show();
                                            isModelDownloading = false;

                                            // Save model downloaded state
                                            SharedPreferencesDictionary.saveModelDownloadedState(TextTranslationActivity.this, Constants.MODEL_DOWNLOADED, true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dismissDialog(snackBarDownloading);
                                        }
                                    })
                                    .addOnCanceledListener(new OnCanceledListener() {
                                        @Override
                                        public void onCanceled() {
                                            dismissDialog(snackBarDownloading);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissDialog(snackBarDownloading);
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            dismissDialog(snackBarDownloading);
                        }
                    });
        }
    }

    private void displayDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.CustomDialogTheme);

        // Map views
        View view = getLayoutInflater().inflate(R.layout.alert_progress_dialogue, null);
        alert.setView(view);

        // Create alert dialog
        waitingDialog = alert.create();
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    private void dismissDialog(Snackbar snackBarDownloading) {
        waitingDialog.dismiss();
        snackBarDownloading.dismiss();

        final Snackbar snackbar = Snackbar.make(constraintLayoutTextTranslation,
                getString(R.string.model_could_not_be_downloaded),
                BaseTransientBottomBar.LENGTH_INDEFINITE);

        snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                downloadModel();
            }
        });
        snackbar.show();
    }

    /**
     * Call intent to return to MainActivity
     */
    private void returnToPreviousActivity() {
        // Return button clicked
        ibBackTextTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show interstitial ad
                AdsManager.showGoogleInterstitialAd(TextTranslationActivity.this);
            }
        });
    }

    @Override
    public void networkAvailable() {
        if (!isModelDownloaded) {
            if (internetConnectionDialog != null) {
                internetConnectionDialog.dismiss();
            }
            downloadModel();
        } else {
            if (waitingDialog != null) {
                waitingDialog.dismiss();
            }
        }
    }

    @Override
    public void networkUnavailable() {
        if (!isModelDownloaded) {
            Snackbar.make(constraintLayoutTextTranslation, getString(R.string.no_internet_connection), BaseTransientBottomBar.LENGTH_SHORT).show();
        }

        if (isModelDownloading) {
            waitingDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        if (!isModelDownloaded) {
            registerNetworkReceiver.unregisterNetworkChangeReceiver(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (!isModelDownloaded) {
            registerNetworkReceiver.registerNetworkChangeReceiver(this);
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_INTERNET_CONNECTION) {
            if (!NetworkUtil.isNetworkConnected(this)) {
                NetworkUtil.displayConnectNetworkDialog(this);
            } else {
                downloadModel();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        // Shutdown tts instances
        localTextToSpeech.shutdown();

        // Stop online tts audio
        googleTextToSpeech.stopPlay();

        // Close translators
        if (translatorEngPor != null) {
            translatorEngPor.close();
        }
        if (translatorPorEng != null) {
            translatorPorEng.close();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Show interstitial ad
        AdsManager.showGoogleInterstitialAd(this);
    }
}