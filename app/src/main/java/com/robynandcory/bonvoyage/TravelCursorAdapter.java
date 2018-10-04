package com.robynandcory.bonvoyage;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import static com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

/**
 * Credit for reference for cursor adapter with recyclerview: https://github.com/dizzy-miss-lizzy/BookInventory
 * https://stackoverflow.com/questions/24471109/recyclerview-onclick
 */
public class TravelCursorAdapter extends RecyclerView.Adapter<TravelCursorAdapter.TravelHolder> {
    private Context context;
    private CursorAdapter cursorAdapter;
    private Uri currentItemUri = null;

    public TravelCursorAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursorAdapter = new CursorAdapter(context, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.card_view_travel_items, parent, false);
            }

            /**
             * Locates view and uses cursor to bind data to views for given context
             */
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView nameTextView = view.findViewById(R.id.list_item_name);
                TextView quantityTextView = view.findViewById(R.id.list_item_quantity);

                int nameColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_NAME);
                int quantityColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_QUANTITY);

                String itemName = cursor.getString(nameColumnIndex);
                String itemQuantity = cursor.getString(quantityColumnIndex);

                nameTextView.setText(itemName);
                quantityTextView.setText(itemQuantity);

                int cursorItemId = cursor.getInt(cursor.getColumnIndex(TravelEntry._ID));
                final Uri contentUri = Uri.withAppendedPath(TravelEntry.CONTENT_URI, Integer.toString(cursorItemId));
                Log.e("TravelCursorAdapter", "bindView is being called");
            }
        };

    }

    public void swapCursor(Cursor newCursor) {
        cursorAdapter.swapCursor(newCursor);
        notifyDataSetChanged();
    }

    class TravelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TravelHolder(View itemCardView) {
            super(itemCardView);
            itemCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, EditorActivity.class);
            intent.setData(currentItemUri);
            context.startActivity(intent);
        }
    }

    /**
     * @param viewGroup parent
     * @param position the view type
     * @return a new viewHolder to inflate the layout
     */
    @Override
    public TravelHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = cursorAdapter.newView(context, cursorAdapter.getCursor(), viewGroup);
        return new TravelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelCursorAdapter.TravelHolder travelHolder, int position) {
        cursorAdapter.getCursor().moveToPosition(position);
        cursorAdapter.bindView(travelHolder.itemView, context, cursorAdapter.getCursor());
        final long currentUri = cursorAdapter.getItemId(position);
        currentItemUri = ContentUris.withAppendedId(TravelEntry.CONTENT_URI, currentUri);
    }

    @Override
    public long getItemId(int position) {
        if (cursorAdapter.getCursor().moveToPosition(position)) {
            return cursorAdapter.getCursor().getColumnIndex(TravelEntry._ID);
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }
}
