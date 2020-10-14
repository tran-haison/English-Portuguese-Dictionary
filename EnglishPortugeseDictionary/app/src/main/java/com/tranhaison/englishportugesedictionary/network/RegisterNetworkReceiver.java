package com.tranhaison.englishportugesedictionary.network;

import android.content.Context;
import android.content.IntentFilter;

public class RegisterNetworkReceiver {

    // Network broadcast receiver
    private NetworkChangeReceiver networkChangeReceiver;

    public RegisterNetworkReceiver() {

    }

    public void startNetworkChangeReceiver(Context currentContext) {
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.addListener((NetworkChangeReceiver.ConnectivityReceiverListener) currentContext);
        registerNetworkChangeReceiver(currentContext);
    }

    /**
     * Register the NetworkStateReceiver with your activity
     * @param currentContext
     */
    public void registerNetworkChangeReceiver(Context currentContext) {
        currentContext.registerReceiver(networkChangeReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     Unregister the NetworkStateReceiver with your activity
     * @param currentContext
     */
    public void unregisterNetworkChangeReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkChangeReceiver);
    }
}
