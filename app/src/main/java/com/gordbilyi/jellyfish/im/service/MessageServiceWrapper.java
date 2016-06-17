package com.gordbilyi.jellyfish.im.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by gordbilyi on 5/26/16.
 */
public class MessageServiceWrapper implements ServiceWrapper {

    private static final String TAG = "MessageServiceWrapper";
    private MessageService mService = null;
    private boolean mBound = false;
    private Context mContext;

    public MessageServiceWrapper(Context context) {
        this.mContext = context;
        bind();
    }

    @Override
    public void bind() {
        Log.d(TAG, "bind");
        mContext.bindService(new Intent(mContext, MessageService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unbind() {
        if (mBound) {
            Log.d(TAG, "unbind");
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean invoke() {
        //Nothing here to invoke on service
        return true;
    }

    public void setCurrentChatId(long chatId) {
        mService.setCurrentChatId(chatId);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public static final String TAG = "ServiceConnection";

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            Log.d(TAG, "onServiceConnected");
            MessageService.LocalBinder binder = (MessageService.LocalBinder) service;
            MessageServiceWrapper.this.mService = binder.getService();
            mBound = true;

            if (!MessageServiceWrapper.this.mService.started) {
                // Once successfully bound to the mService start it to have it running independently on
                // activity is alive or not
                mContext.startService(new Intent(mContext, MessageService.class));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            mService = null; // to be garbage collected
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
