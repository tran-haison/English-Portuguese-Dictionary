package com.tranhaison.englishportugesedictionary.utils.texttospeech;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleTextToSpeech {

    private Context context;
    private MediaPlayer mediaPlayer;
    private static String TSS_USER_AGENT = "TW96aWxsYS80LjAgKGNvbXBhdGlibGU7IE1TSUUgOC4wOyBXaW5kb3dzIE5UIDUuMDsgLk5FVCBDTFIgMS4xLjQzMjI7IC5ORVQgQ0xSIDIuMC41MDIxNTsp";
    private static String TSS_LINK = "aHR0cHM6Ly90cmFuc2xhdGUuZ29vZ2xlLmNvbS90cmFuc2xhdGVfdHRzP3RrPTI4MzgwMCZjbGllbnQ9dHctb2ImdG90YWw9MSZpZHg9MCZxPQ==";

    public GoogleTextToSpeech(Context context) {
        this.context = context;
    }

    public void play(final String text, final String lang) {
        Thread x = new Thread() {
            public void run() {
                stopPlay();

                mediaPlayer = new MediaPlayer();
                String text2 = text.replace(' ', '+')
                        .replace('\n', '.') + "%0A";
                try {
                    String url1 = (new String(Base64.decode(TSS_LINK, Base64.DEFAULT))) + text2 +
                            "&tl=" + lang + "&textlen=" + text2.length();
                    File file = new File(context.getCacheDir(), text2 + lang + ".mp3");
                    if (!file.exists()) {
                        if (NetworkUtil.isNetworkConnected(context)) {
                            DownloadFile(url1, file);
                            mediaPlayer = new MediaPlayer();

                            try {
                                mediaPlayer.setDataSource(file.getAbsolutePath());
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("downloaded", e.getMessage());
                            }

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mediaPlayer.release();
                                }
                            });
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(file.getAbsolutePath());
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.release();
                            }
                        });
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    mediaPlayer.reset();
                }
            }
        };
        x.start();
    }

    /**
     * Download file
     * @param u
     * @param outputFile
     */
    private void DownloadFile(String u, File outputFile) {
        try {
            HttpURLConnection connection;

            URL url = new URL(u);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", (new String(Base64.decode(TSS_USER_AGENT, Base64.DEFAULT))));
            connection.connect();

            DataInputStream stream = new DataInputStream(connection.getInputStream());

            byte[] buffer = new byte[connection.getContentLength()];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
