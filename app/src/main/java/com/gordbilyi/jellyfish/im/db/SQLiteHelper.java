package com.gordbilyi.jellyfish.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gordbilyi on 4/29/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public final static String TABLE_CHATS = "chats";
    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_FROM = "fromJID";
    public final static String COLUMN_TO = "toJID";
    public final static String COLUMN_GROUP = "groupJID";
    public final static String TABLE_MESSAGES = "messages";

    public final static String COLUMN_CHAT_ID = "chat_id";
    public final static String COLUMN_BODY = "body";
    public final static String COLUMN_TIMESTAMP = "timestamp";
    public final static String COLUMN_IS_LOCAL = "is_local";

    private static final String DATABASE_NAME = "im.db"; // instant messaging
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CHATS_CREATE = "create table if not exists chats " +
            " (_id integer primary key, fromJID text, toJID text, groupJID text, timestamp long not null)";

    private static final String TABLE_MESSAGES_CREATE = "create table if not exists "
            + TABLE_MESSAGES +
            " (chat_id integer references chats(_id), _id integer primary key, body text, is_local integer default 0, timestamp long not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // !! is not called if DB already exists!
        db.execSQL(TABLE_CHATS_CREATE);
        db.execSQL(TABLE_MESSAGES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }
}
