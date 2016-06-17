package com.gordbilyi.jellyfish.im.ui.activity;

import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gordbilyi.jellyfish.im.service.MessageService;
import com.gordbilyi.jellyfish.im.ui.adapter.MessageCursorAdapter;
import com.gordbilyi.jellyfish.im.utils.ActivityUtils;
import com.gordbilyi.jellyfish.R;
import com.gordbilyi.jellyfish.im.db.SQLiteHelper;
import com.gordbilyi.jellyfish.im.db.provider.MessageProvider;
import com.gordbilyi.jellyfish.im.domain.Message;

// ListActivity cannot be used as it doesn't support Toolbar
public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MessagesActivity";
    private EditText messageInput;
    private MessageService mService;
    private boolean mBound = false;
    private RecyclerView recyclerView;

    private long chatId = 0; // valid ids start from 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MessagesActivity.onCreate");
        setContentView(R.layout.activity_messages);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            chatId = getIntent().getExtras().getLong(ActivityUtils.EXTRA_CHAT_ID);
            getSupportActionBar().setTitle(getIntent().getExtras().getString(ActivityUtils.EXTRA_CHAT_NAME));
        }

        // bind to the service again, at this point 2 connections to the server
        bindService(new Intent(this, MessageService.class), mConnection,
                Context.BIND_AUTO_CREATE);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        messageInput = (EditText) findViewById(R.id.messageEditText);
        ImageButton sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MessageCursorAdapter(getBaseContext()));

        // Load the content, get only messages for the opened chatId
        getLoaderManager().initLoader(1, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String selection = SQLiteHelper.COLUMN_CHAT_ID + "=?";
                String[] selectionArgs = {String.valueOf(chatId)};
                return new CursorLoader(
                        MessagesActivity.this,
                        MessageProvider.URI_MESSAGES,
                        MessageProvider.allColumns,
                        selection,
                        selectionArgs,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                ((MessageCursorAdapter) recyclerView.getAdapter()).swapCursor(c);

                // scroll to show the message at the bottom, -1 is the key here
                recyclerView.getLayoutManager().scrollToPosition(c.getCount() - 1);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((MessageCursorAdapter) recyclerView.getAdapter()).swapCursor(null);
            }
        });

    }

    @Override
    public void onClick(View view) {
        sendTextMessage();
    }

    private void sendTextMessage() {
        String message = messageInput.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final Message chatMessage = new Message();
            chatMessage.setTimestamp(System.currentTimeMillis());
            chatMessage.setBody(message);
            chatMessage.setChatId(chatId);
            chatMessage.setLocal(true);
            messageInput.setText("");

            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_BODY, chatMessage.getBody());
            values.put(SQLiteHelper.COLUMN_TIMESTAMP, chatMessage.getTimestamp()); // current time
            values.put(SQLiteHelper.COLUMN_IS_LOCAL, chatMessage.isLocal() ? 1 : 0);
            values.put(SQLiteHelper.COLUMN_CHAT_ID, chatId);

            // once insert is successful, loader updates the list automatically!
            getContentResolver().insert(MessageProvider.URI_MESSAGES, values);
            mService.sendMessage(chatMessage);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        public static final String TAG = "ServiceConnection";

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            MessageService.LocalBinder binder = (MessageService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setCurrentChatId(chatId);
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            mService = null; // to be garbage collected
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed");
        unbindService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                unbindService();
                NavUtils.navigateUpFromSameTask(this);
                return true;
//            case R.id.nav_exit:
//                // handle other menu items clicks
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unbindService() {
        Log.d(TAG, "unbinding service..");
        if (mBound) {
            unbindService(mConnection);
        }
    }
}
