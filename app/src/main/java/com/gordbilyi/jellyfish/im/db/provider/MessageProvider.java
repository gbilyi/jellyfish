package com.gordbilyi.jellyfish.im.db.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.gordbilyi.jellyfish.im.db.SQLiteHelper;

/**
 * Created by gordbilyi on 5/10/16.
 */
public class MessageProvider extends ContentProvider {

    // All URIs share these parts
    public static final String AUTHORITY = "com.gordbilyi.jellyfish.message.provider";
    public static final String SCHEME = "content://";

    // URIs
    // Used for all messages
    public static final String MESSAGES = SCHEME + AUTHORITY + "/messages";
    public static final Uri URI_MESSAGES = Uri.parse(MESSAGES);

    public static String[] allColumns = {
            SQLiteHelper.COLUMN_CHAT_ID, SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_BODY, SQLiteHelper.COLUMN_IS_LOCAL,
            SQLiteHelper.COLUMN_TIMESTAMP};

    private SQLiteHelper helper;

    @Override
    public boolean onCreate() {
        helper = new SQLiteHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        if (URI_MESSAGES.equals(uri)) {
            result = helper
                    .getReadableDatabase()
                    .query(SQLiteHelper.TABLE_MESSAGES, projection, selection, selectionArgs, null,
                            null, sortOrder, null);
            result.setNotificationUri(getContext().getContentResolver(), URI_MESSAGES);
        }  else {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        long messageId = helper
                .getReadableDatabase().insert(SQLiteHelper.TABLE_MESSAGES, null, contentValues);

        if (messageId > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, messageId);
            getContext().getContentResolver().notifyChange(newUri, null);
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
