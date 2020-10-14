package com.tranhaison.englishportugesedictionary.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.activities.TextTranslationActivity;
import com.tranhaison.englishportugesedictionary.utils.Constants;

public class NetworkUtil {

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check the connectivity of the network
     * Both WIFI and Mobile
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Display a dialog to inform user to connect to the Internet
     * @param activity
     * @return
     */
    public static AlertDialog displayConnectNetworkDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomDialog);

        // Set view
        View view = activity.getLayoutInflater().inflate(R.layout.alert_connect_internet_dialog, null);
        Button btnConnectInternet = view.findViewById(R.id.btnConnectInternet);
        Button btnCancelConnectInternet = view.findViewById(R.id.btnCancelConnectInternet);
        TextView tvPromptConnectInternet = view.findViewById(R.id.tvPromptConnectInternet);
        builder.setView(view);

        // Create alert dialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        if (activity instanceof TextTranslationActivity) {
            tvPromptConnectInternet.setText(activity.getResources().getString(R.string.please_connect_to_wifi_to_download_translation_model));
        }

        btnCancelConnectInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                activity.onBackPressed();
            }
        });

        btnConnectInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), Constants.REQUEST_INTERNET_CONNECTION);
            }
        });

        alertDialog.show();
        return alertDialog;
    }

}
