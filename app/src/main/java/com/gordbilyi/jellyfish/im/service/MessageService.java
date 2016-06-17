package com.gordbilyi.jellyfish.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gordbilyi.jellyfish.im.domain.Message;
import com.gordbilyi.jellyfish.im.service.connection.ConnectionManager;
import com.gordbilyi.jellyfish.im.service.connection.SSL;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static com.gordbilyi.jellyfish.im.utils.CommonUtils.PASSWORD;
import static com.gordbilyi.jellyfish.im.utils.CommonUtils.SERVICE_NAME;
import static com.gordbilyi.jellyfish.im.utils.CommonUtils.USERNAME;

/**
 * Created by gordbilyi on 4/25/16.
 */
public class MessageService extends Service {

    private static final String TAG = "MessageService";

    // This global var needed to determine when service was started
    // by the system vs it was bound by activity
    public static boolean started = false;

    private static ConnectionManager connectionManager;
    private final IBinder mBinder = new LocalBinder();

    private long currentChatId = 0; // valid ids start from 1

    public void setCurrentChatId(long chatId) {
        this.currentChatId = chatId;
        if (connectionManager != null) {
            connectionManager.setCurrentChatId(chatId);
        }
    }

    public void sendMessage(Message chatMessage) {
        connectionManager.sendMessage(currentChatId, chatMessage);
        // once successfully sent update timestamp
        connectionManager.updateChatTimestamp(currentChatId);
    }

    public void getRoster() {
        connectionManager.getRoster();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        started = true;

        int result = super.onStartCommand(intent, flags, startId);
        ;
        if (intent == null) {
            return result;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                connectionManager = ConnectionManager
                        .getInstance(MessageService.this, getConnectionConfiguration());
                connectionManager.connect();
                return null;
            }
        }.execute();
        return result;
    }

    @NonNull
    private XMPPTCPConnectionConfiguration.Builder getConnectionConfiguration() {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setUsernameAndPassword(USERNAME, PASSWORD);
//        config.setHost(HOST);

        try {
            config.setXmppDomain(JidCreate.domainBareFrom(SERVICE_NAME));
        } catch (XmppStringprepException e) {
            Log.e(TAG, e.getMessage());
        }

        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        config.setCustomSSLContext(SSL.getSSLContext());
        return config;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy dropping XMPP connection");
        if(connectionManager != null) {
            connectionManager.disconnect();
        }
    }

    public class LocalBinder<S> extends Binder {
        public MessageService getService() {
            return MessageService.this;
        }
    }

    // unused yet
    public void logout() {
        connectionManager.disconnect();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
    }
}