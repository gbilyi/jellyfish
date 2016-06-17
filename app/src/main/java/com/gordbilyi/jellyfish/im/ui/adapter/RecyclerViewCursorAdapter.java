package com.gordbilyi.jellyfish.im.ui.adapter;


import android.content.Context;
import android.database.Cursor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by gordbilyi on 5/13/16.
 *
 * Adapter which allows usage of cursorloader along with recyclerview
 * (there is none supplied by google by default)
 */
public abstract class RecyclerViewCursorAdapter<T extends RecyclerViewCursorViewHolder>
        extends RecyclerView.Adapter<T> {
    protected final Context mContext;
    protected CursorAdapter mCursorAdapter;
    private T mViewHolder;
    private LayoutInflater mInflater;

    protected RecyclerViewCursorAdapter(Context context) {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected void setupCursorAdapter(Cursor cursor, int flags,
                                      final int resource, final boolean attachToRoot) {
        this.mCursorAdapter = new CursorAdapter(mContext, cursor, flags) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return mInflater.inflate(resource, parent, attachToRoot);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Bind cursor to our ViewHolder
                mViewHolder.bindCursor(cursor);
            }
        };
    }

    public void swapCursor(Cursor cursor) {
        this.mCursorAdapter.swapCursor(cursor);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    protected void setViewHolder(T viewHolder) {
        this.mViewHolder = viewHolder;
    }
}
