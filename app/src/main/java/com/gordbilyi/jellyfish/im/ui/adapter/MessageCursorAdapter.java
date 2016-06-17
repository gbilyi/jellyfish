package com.gordbilyi.jellyfish.im.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gordbilyi.jellyfish.R;
import com.gordbilyi.jellyfish.im.db.SQLiteHelper;

/**
 * Created by gordbilyi on 5/11/16.
 */
public class MessageCursorAdapter extends RecyclerViewCursorAdapter<MessageCursorAdapter.ViewHolder> {

    /**
     * Constructor.
     *
     * @param context The Context the Adapter is displayed in.
     */
    public MessageCursorAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.message_bubble, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
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
//        public TextView layout;
//        public TextView parent_layout;
        public TextView message;
        private boolean isLocal;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message_text);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            message.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_BODY)));
            isLocal = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_IS_LOCAL)) == 1;

            // parent layout props are taken into consideration if it's been properly inflated
            // e.g. parent was passed correctly
            LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.bubble_layout);
            LinearLayout parent_layout = (LinearLayout) itemView.findViewById(R.id.bubble_layout_parent);

            if (isLocal) {
                layout.setBackgroundResource(R.drawable.chat_bubble2);
                parent_layout.setGravity(Gravity.RIGHT);
            } else {
                layout.setBackgroundResource(R.drawable.chat_bubble1);
                parent_layout.setGravity(Gravity.LEFT);
            }

        }
    }
}
