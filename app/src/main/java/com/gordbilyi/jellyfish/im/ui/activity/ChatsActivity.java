package com.gordbilyi.jellyfish.im.ui.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gordbilyi.jellyfish.R;
import com.gordbilyi.jellyfish.im.db.SQLiteHelper;
import com.gordbilyi.jellyfish.im.db.provider.ChatProvider;
import com.gordbilyi.jellyfish.im.service.MessageServiceWrapper;
import com.gordbilyi.jellyfish.im.service.ServiceWrapper;
import com.gordbilyi.jellyfish.im.ui.adapter.ChatCursorAdapter;
import com.gordbilyi.jellyfish.im.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.gordbilyi.jellyfish.im.utils.ActivityUtils.EXTRA_CHAT_ID;
import static com.gordbilyi.jellyfish.im.utils.ActivityUtils.EXTRA_CHAT_NAME;

public class ChatsActivity extends AppCompatActivity {

    private static final String TAG = "ChatsActivity";

    private RecyclerView recyclerView = null;
    private List<ServiceWrapper> services = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Log.d(TAG, "onCreate(): start");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(CommonUtils.USERNAME);
        setSupportActionBar(myToolbar);


        final MessageServiceWrapper msgServiceWrapper = new MessageServiceWrapper(getBaseContext());
        services.add(msgServiceWrapper);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setAdapter(new ChatCursorAdapter(getBaseContext(),
                new ChatCursorAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(long chatId, final String chatName) {
                        Intent intent = new Intent(getBaseContext(), MessagesActivity.class);
                        intent.putExtra(EXTRA_CHAT_ID, chatId);
                        intent.putExtra(EXTRA_CHAT_NAME, chatName);
                        msgServiceWrapper.setCurrentChatId(chatId);
                        startActivity(intent);
                    }
                }));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(
                        ChatsActivity.this,
                        ChatProvider.URI_CHATS,
                        ChatProvider.allColumns,
                        null,
                        null,
                        SQLiteHelper.COLUMN_TIMESTAMP + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                ((ChatCursorAdapter) recyclerView.getAdapter()).swapCursor(c);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((ChatCursorAdapter) recyclerView.getAdapter()).swapCursor(null);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chats_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_message:
                Toast.makeText(getBaseContext(), "show Contacts Activity to message somebody",
                        Toast.LENGTH_LONG).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Is called as well when back button is clicked
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        for (ServiceWrapper serviceW : services) {
            serviceW.unbind();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Is called when Home button on messages activity action bar is called
     * chats activity is not recreated as it marked as singleTop
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent(): start");
        // TODO: any info communicated from messages activity comes here
        // FIXME: Listeners to be replaced by this?
        super.onNewIntent(intent);
    }




}
