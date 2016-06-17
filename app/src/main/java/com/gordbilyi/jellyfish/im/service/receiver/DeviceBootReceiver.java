package com.gordbilyi.jellyfish.im.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gordbilyi.jellyfish.im.service.MessageService;

public class DeviceBootReceiver extends BroadcastReceiver {
    public final static String TAG = "DeviceBootReceiver";

    public DeviceBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MessageService.class);
        context.startService(startServiceIntent);
    }
}
