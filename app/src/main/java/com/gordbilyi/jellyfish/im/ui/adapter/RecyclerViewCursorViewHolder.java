package com.gordbilyi.jellyfish.im.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gordbilyi on 5/13/16.
 */
public abstract class RecyclerViewCursorViewHolder extends RecyclerView.ViewHolder {

    public RecyclerViewCursorViewHolder(View view) {
        super(view);
    }
    public abstract void bindCursor(Cursor cursor);
}
