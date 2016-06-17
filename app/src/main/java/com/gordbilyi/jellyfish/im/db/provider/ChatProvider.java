package com.gordbilyi.jellyfish.im.db.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.gordbilyi.jellyfish.im.db.SQLiteHelper;
import com.gordbilyi.jellyfish.im.domain.Chat;

/**
 * Created by gordbilyi on 5/1/16.
 */
public class ChatProvider extends ContentProvider {

    // All URIs share these parts
    public static final String AUTHORITY = "com.gordbilyi.jellyfish.chat.provider";
    public static final String SCHEME = "content://";

    // URIs
    // Used for all chats
    public static final String CHATS = SCHEME + AUTHORITY + "/chats";
    public static final Uri URI_CHATS = Uri.parse(CHATS);

    public static String[] allColumns = {SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_FROM, SQLiteHelper.COLUMN_TO,
            SQLiteHelper.COLUMN_GROUP, SQLiteHelper.COLUMN_TIMESTAMP};

    private SQLiteHelper helper;

    @Override
    public boolean onCreate() {
        helper = new SQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor result;
        if (URI_CHATS.equals(uri)) {
            result = helper.getReadableDatabase()
                    .query(SQLiteHelper.TABLE_CHATS, projection, selection, selectionArgs, null,
                            null, sortOrder, null);
            result.setNotificationUri(getContext().getContentResolver(), URI_CHATS);
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long chatId = helper.getReadableDatabase().insert(SQLiteHelper.TABLE_CHATS, null, values);

        if (chatId > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, chatId);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause,
                      String[] whereArgs) {
        int rowsAffected = helper.getWritableDatabase().update(SQLiteHelper.TABLE_CHATS, values,
                whereClause, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Chat cursorToChat(Cursor cursor) {
        Chat chat = new Chat();
        chat.setId(cursor.getLong(0));
        chat.setFrom(cursor.getString(1));
        chat.setTo(cursor.getString(2));
        chat.setGroup(cursor.getString(3));
        chat.setTimestamp(cursor.getLong(4));
        return chat;
    }
}
