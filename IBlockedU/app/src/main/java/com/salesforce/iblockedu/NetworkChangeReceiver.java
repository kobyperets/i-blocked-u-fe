package com.salesforce.iblockedu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kperets on 29/11/2017.
 */

class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "NetworkChangeReceiver";
    private final IBlockedUMainActivity mainActivity;
    private boolean isConnected = false;

    public NetworkChangeReceiver(IBlockedUMainActivity mainActivity) {
        this.mainActivity= mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "Received notification about network status");
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            Log.v(LOG_TAG, "Now you are connected to Internet!");
                            Toast.makeText(context, "Internet available via Broadcast receiver", Toast.LENGTH_SHORT).show();
                            isConnected = true;

                            SharedPreferences settings = mainActivity.getSharedPreferences(IBlockedUMainActivity.PREFS_NAME, Context.MODE_PRIVATE);
                            String email = settings.getString("offlline_email", "");
                            String licensePlate = settings.getString("offline_name", "");
                            mainActivity.processBlockRequest(this, email, licensePlate);
                        }
                        return true;
                    }
                }
            }
        }
        Log.v(LOG_TAG, "You are not connected to Internet!");
        Toast.makeText(context, "Internet NOT available via Broadcast receiver", Toast.LENGTH_SHORT).show();
        isConnected = false;
        return false;
    }
}
