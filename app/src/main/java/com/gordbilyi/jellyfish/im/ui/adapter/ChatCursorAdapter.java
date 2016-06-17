package com.gordbilyi.jellyfish.im.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gordbilyi.jellyfish.R;
import com.gordbilyi.jellyfish.im.db.SQLiteHelper;
import com.gordbilyi.jellyfish.im.utils.CommonUtils;

/**
 * Created by gordbilyi on 5/11/16.
 */
public class ChatCursorAdapter extends RecyclerViewCursorAdapter<ChatCursorAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(long chatId, final String chatName);
    }
    private final OnItemClickListener listener;

    public ChatCursorAdapter(Context context, OnItemClickListener listener) {
        super(context);
        this.listener = listener;
        setupCursorAdapter(null, 0, R.layout.chat_item, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent), listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    public static class ViewHolder extends RecyclerViewCursorViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView primaryText;
        public TextView secondaryText;

        public long chatId;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView, final OnItemClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(chatId, primaryText.getText().toString());
                }
            });

            primaryText = (TextView) itemView.findViewById(R.id.primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.secondary_text);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            chatId = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
            primaryText.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_TO)));
            secondaryText.setText(CommonUtils.getFormattedDate(
                    cursor.getLong(cursor.getColumnIndex(SQLiteHelper.COLUMN_TIMESTAMP))));
        }
    }
}
