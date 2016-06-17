package com.gordbilyi.jellyfish.im.service.connection;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

/**
 * Created by gordbilyi on 4/24/16.
 */
public class ConnectionListener implements org.jivesoftware.smack.ConnectionListener {

    private static final String TAG = "ConnectionListener";
    private ConnectionManager connectionManager;

    public ConnectionListener(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void connected(final XMPPConnection connection) {
       Log.d(TAG, "connected");
        if (!connection.isAuthenticated()) {
            try {
                 ((XMPPTCPConnection) connection).login();
                Log.d(TAG, "logged in");
            } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG, "authenticated");
        connectionManager.onAuthenticated();
    }

    @Override
    public void connectionClosed() {
        Log.d(TAG, "connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e(TAG, "connectionClosedOnError " + e.getMessage());
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(TAG, "reconnectingIn " + seconds);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.e(TAG, "reconnectionFailed " + e.getMessage());
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d(TAG, "ReconnectionSuccessful");
    }

}
