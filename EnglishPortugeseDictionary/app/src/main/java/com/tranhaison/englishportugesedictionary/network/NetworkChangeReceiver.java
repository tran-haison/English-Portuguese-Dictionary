package com.tranhaison.englishportugesedictionary.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {

    protected List<ConnectivityReceiverListener> listeners;
    protected Boolean connected;
    private String TAG = "NetworkStateReceiver";

    public NetworkChangeReceiver() {
        listeners = new ArrayList<>();
        connected = null;
    }

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast (event for which the broadcast receiver has registered occurs).
     * During this time you can use the other methods on BroadcastReceiver to view/modify the current result values.
     * NOTE: When it runs on the main thread you should never perform long-running operations in it (there is a timeout of 10 seconds that the system allows before considering the receiver to be blocked and a candidate to be killed).
     * NOTE: You cannot launch a popup dialog in your implementation of onReceive().
     *
     * @param context Object to access additional information or to start services or activities
     * @param intent  Object with action used to register your receiver. This object contains additional information (e.g. extras)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent broadcast received");
        if (intent == null || intent.getExtras() == null) {
            return;
        }

        // Retrieve a ConnectivityManager for handling management of network connections
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Details about the currently active default data network. When connected, this network is the default route for outgoing connections
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        /**
         * NOTE: getActiveNetworkInfo() may return null when there is no default network e.g. Airplane Mode
         */
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            // Boolean that indicates whether there is a complete lack of connectivity
            connected = false;
        }

        // Trigger function
        notifyStateToAll();

        // After the onReceive() of the receiver class has finished, the Android system is allowed to recycle the receiver
    }

    /**
     * Notify the state to all needed methods
     */
    private void notifyStateToAll() {
        Log.i(TAG, "Notifying state to " + listeners.size() + " listener(s)");
        for (ConnectivityReceiverListener connectivityReceiverListener : listeners) {
            notifyState(connectivityReceiverListener);
        }
    }

    /**
     * Notify the network state, triggering interface functions based on the current state
     *
     * @param connectivityReceiverListener Object which implements the NetworkStateReceiverListener interface
     */
    private void notifyState(ConnectivityReceiverListener connectivityReceiverListener) {
        if (connected == null || connectivityReceiverListener == null) {
            return;
        }

        if (connected == true) {
            // Triggering function on the interface towards network availability
            connectivityReceiverListener.networkAvailable();
        } else {
            // Triggering function on the interface towards network being unavailable
            connectivityReceiverListener.networkUnavailable();
        }
    }

    /**
     * Adds a listener to the list so that it will receive connection state change updates
     *
     * @param connectivityReceiverListener Object which implements the NetworkStateReceiverListener interface
     */
    public void addListener(ConnectivityReceiverListener connectivityReceiverListener) {
        listeners.add(connectivityReceiverListener);
        notifyState(connectivityReceiverListener);
    }

    /**
     * Removes listener (when no longer necessary) from the list so that it will no longer receive connection state change updates
     *
     * @param connectivityReceiverListener Object which implements the NetworkStateReceiverListener interface
     */
    public void removeListener(ConnectivityReceiverListener connectivityReceiverListener) {
        listeners.remove(connectivityReceiverListener);
    }

    public interface ConnectivityReceiverListener {
        /**
         * When the connection state is changed and there is a connection, this method is called
         */
        void networkAvailable();

        /**
         * Connection state is changed and there is not a connection, this method is called
         */
        void networkUnavailable();
    }

}
