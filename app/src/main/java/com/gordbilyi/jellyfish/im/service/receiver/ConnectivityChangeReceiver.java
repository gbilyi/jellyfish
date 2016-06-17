package com.gordbilyi.jellyfish.im.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gordbilyi.jellyfish.im.service.MessageService;
import com.gordbilyi.jellyfish.im.utils.ActivityUtils;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "ConChangeReceiver";


    public ConnectivityChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.d(TAG, "isConnected = " + isConnected);
        if (isConnected) {
            // pass connectivity info to the service
            Intent startServiceIntent = new Intent(context, MessageService.class);
            startServiceIntent.putExtra(ActivityUtils.EXTRA_IS_CONNECTED, isConnected);
            context.startService(startServiceIntent);
        }
    }
}
