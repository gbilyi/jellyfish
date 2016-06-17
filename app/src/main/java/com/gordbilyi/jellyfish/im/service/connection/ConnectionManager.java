package com.gordbilyi.jellyfish.im.service.connection;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.gordbilyi.jellyfish.im.db.provider.ChatProvider;
import com.gordbilyi.jellyfish.im.db.SQLiteHelper;
import com.gordbilyi.jellyfish.im.db.provider.MessageProvider;
import com.gordbilyi.jellyfish.im.domain.Message;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.gordbilyi.jellyfish.im.utils.CommonUtils.USER_JID;

/**
 * Created by gordbilyi on 4/25/16.
 * <p/>
 * Singleton. Responsible for maintaining a connection to XMPP server.
 */
public class ConnectionManager {

    private static final String TAG = "ConnectionManager";

    private XMPPTCPConnection connection;
    private static ConnectionManager instance = null;

    XMPPTCPConnectionConfiguration.Builder connectionConfig;

    // a lookup map for chats by chatId
    private Map<Long, com.gordbilyi.jellyfish.im.domain.Chat> chatsMap = new HashMap<>();

    private Context context;

    // is used to determine which chat should receive messages
    // Scenario: Chat #2 is opened, Chat #1 receives message - message should not be added
    // to messages stack (just shown as a toast/notification)
    private long currentChatId = 0;

    private ConnectionManager(Context context, XMPPTCPConnectionConfiguration.Builder connectionConfig) {
        this.context = context;
        this.connectionConfig = connectionConfig;
        init();
    }

    public static ConnectionManager getInstance(Context context, XMPPTCPConnectionConfiguration.Builder connectionConfig) {
        if (instance == null) {
            instance = new ConnectionManager(context, connectionConfig);
        }
        return instance;
    }

    public void init() {
        connection = new XMPPTCPConnection(connectionConfig.build());
        ConnectionListener connectionListener =
                new ConnectionListener(this);
        connection.addConnectionListener(connectionListener);
    }

    public void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                connection.disconnect();
                Log.d(TAG, "disconnect");
                return null;
            }
        }.execute();
    }

    public void connect() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                // same object here after onStop() and onCreate() of ChatsActivity is called
                if (connection.isConnected()) {
                    return false;
                }
                try {
                    connection.connect();
                } catch (IOException | SmackException | XMPPException | InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                return false;
            }
        }.execute();
    }

    public void sendMessage(long currentChatId, Message chatMessage) {
        try {
            Log.d(TAG, "messaged sent: " + chatMessage.getBody());

            if (!chatsMap.isEmpty()) {
                chatsMap.get(currentChatId).getSmackChat().sendMessage(chatMessage.getBody());
            } else {
                // comes here when no internet connection
                // which results in no chats got registered
            }
        } catch (NotConnectedException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // not used yet
    public void getRoster() {
        Roster roster = Roster.getInstanceFor(connection);
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            Log.i(TAG, entry.getUser() + " " + entry.getGroups().get(0).getName());

        }
    }

    public void onAuthenticated() {
        registerChats();
    }

    /**
     * Takes care of newly created chats on other side
     */
    private void registerIncomingChats() {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        /*
                        Here a new chat is created. This method is called once user started
                        typing on the other end. This should show the chat on UI.
                         */
                        if (!createdLocally) {
                            Log.d(TAG, "Chat created locally");
                            final String to = chat.getParticipant().asEntityBareJidString();
                            showToast(context, "new chat with " + to);

                            com.gordbilyi.jellyfish.im.domain.Chat newChat = new com.gordbilyi.jellyfish.im.domain.Chat();
                            newChat.setFrom(USER_JID);
                            newChat.setTo(to);
                            newChat.setTimestamp(System.currentTimeMillis());

                            ContentValues values = new ContentValues();
                            values.put(SQLiteHelper.COLUMN_FROM, newChat.getFrom());
                            values.put(SQLiteHelper.COLUMN_TO, newChat.getTo());
                            values.put(SQLiteHelper.COLUMN_GROUP, newChat.getGroup());
                            values.put(SQLiteHelper.COLUMN_TIMESTAMP, newChat.getTimestamp());
                            Uri newChatUri = context.getContentResolver().insert(ChatProvider.URI_CHATS, values);

                            long chatId = ContentUris.parseId(newChatUri);
                            newChat.setId(chatId);
                            newChat.setSmackChat(chat);
                            chatsMap.put(newChat.getId(), newChat);
                            chat.addMessageListener(new LocalChatMessageListener(newChat));
                        } else {
                            Log.d(TAG, "Chat created remotely");
                        }
                    }
                });
    }

    /**
     * Creates a map in memory and assigns listeners to each chat
     * Once connection is established chats are taken from DB, created in memory
     * and listeners assigned.
     */
    public void registerChats() {
        Cursor cursor = context.getContentResolver().query(ChatProvider.URI_CHATS, ChatProvider.allColumns, null, null, null);
        while (cursor.moveToNext()) {
            com.gordbilyi.jellyfish.im.domain.Chat pocChat = ChatProvider.cursorToChat(cursor);

            try {
                Chat c = ChatManager.getInstanceFor(connection)
                        .createChat(JidCreate.entityBareFrom(pocChat.getTo()),
                                new LocalChatMessageListener(pocChat));
                pocChat.setSmackChat(c);
            } catch (XmppStringprepException e) {
                Log.e(TAG, e.getMessage());
            }
            chatsMap.put(pocChat.getId(), pocChat);
        }

        // this needs to kick necessary listener
        registerIncomingChats();
    }

    public void setCurrentChatId(long chatId) {
        this.currentChatId = chatId;
    }

    private class LocalChatMessageListener implements ChatMessageListener {
        private com.gordbilyi.jellyfish.im.domain.Chat pocChat;

        public LocalChatMessageListener(com.gordbilyi.jellyfish.im.domain.Chat pocChat) {
            this.pocChat = pocChat;
        }

        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {

            if (message.getBody() == null) {
                // TODO: investigate why some message bodies are null
                return;
            }

            //TODO: implement builder pattern for domain Message and Chat classes
            final Message m = new Message();
            m.setTimestamp(System.currentTimeMillis());
            m.setBody(message.getBody());
            m.setLocal(false);

            Log.d(TAG, String.format("chatId %s message <%s> received from %s",
                    currentChatId, m.getBody(), chat.getParticipant()));

            updateChatTimestamp(pocChat.getId());

            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_BODY, m.getBody());
            values.put(SQLiteHelper.COLUMN_TIMESTAMP, m.getTimestamp()); // current time
            values.put(SQLiteHelper.COLUMN_IS_LOCAL, m.isLocal() ? 1 : 0);
            values.put(SQLiteHelper.COLUMN_CHAT_ID, pocChat.getId());

            // once insert is successful, loader updates the list automatically
            context.getContentResolver().insert(MessageProvider.URI_MESSAGES, values);

            // show the toast regardless if messages UI is opened
            showToast(context, m.getBody());
        }
    }

    public void updateChatTimestamp(long chatId) {
        ContentValues newValues = new ContentValues();
        long newTime = System.currentTimeMillis();
        newValues.put(SQLiteHelper.COLUMN_TIMESTAMP, newTime);
        String[] args = new String[]{String.valueOf(chatId)};
        int result = context.getContentResolver()
                .update(ChatProvider.URI_CHATS, newValues, SQLiteHelper.COLUMN_ID + "=?", args);
        if (result == 0) {
            Log.w(TAG, "nothing updated");
        }
    }

    private void showToast(final Context ctx, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}