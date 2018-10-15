package com.robynandcory.bonvoyage;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

/**
 * Credit for reference for cursor adapter with recyclerview: https://github.com/dizzy-miss-lizzy/BookInventory
 * https://stackoverflow.com/questions/24471109/recyclerview-onclick
 */
public class TravelCursorAdapter extends RecyclerView.Adapter<TravelCursorAdapter.TravelHolder> {
    private Context context;
    private CursorAdapter cursorAdapter;

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
            public void bindView(View view, final Context context, Cursor cursor) {
                TextView nameTextView = view.findViewById(R.id.list_item_name);
                TextView quantityTextView = view.findViewById(R.id.list_item_quantity);
                TextView priceTextView = view.findViewById(R.id.list_item_price_amount);

                int nameColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_NAME);
                final int quantityColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_QUANTITY);
                int priceColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_PRICE);

                final String itemName = cursor.getString(nameColumnIndex);
                final String itemQuantity = cursor.getString(quantityColumnIndex);
                int itemPrice = cursor.getInt(priceColumnIndex);

                nameTextView.setText(itemName);
                quantityTextView.setText(itemQuantity);
                priceTextView.setText(formatPrice(itemPrice));
                int cursorItemId = cursor.getInt(cursor.getColumnIndex(TravelEntry._ID));
                //contentUri of single item
                final Uri contentUri = Uri.withAppendedPath(TravelEntry.CONTENT_URI, Integer.toString(cursorItemId));
                Button saleButton = view.findViewById(R.id.sale_button);
                saleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantityInt = Integer.parseInt(itemQuantity);
                        if (quantityInt > 0) {
                            quantityInt -= 1;
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TravelEntry.COLUMN_QUANTITY, quantityInt);
                            context.getContentResolver().update(contentUri, contentValues, null, null);
                        } else {
                            Toast.makeText(context, R.string.cannot_set_negative_amount, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }

    /**
     * Formats the price from cents to dollars and cents.
     *
     * @param intPrice cleaned int from database
     * @return Price in US dollar format, e.g. $8.99
     */
    private String formatPrice(int intPrice) {
        int dollars = intPrice / 100;
        int cents = intPrice % 100;
        return String.format(Locale.US, "$%d.%02d", dollars, cents);
    }

    public void swapCursor(Cursor newCursor) {
        cursorAdapter.swapCursor(newCursor);
        notifyDataSetChanged();
    }

    class TravelHolder extends RecyclerView.ViewHolder {
        public TravelHolder(View itemCardView) {
            super(itemCardView);
        }
    }

    /**
     * @param viewGroup parent
     * @param position  the view type
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
        final Uri intentUri = ContentUris.withAppendedId(TravelEntry.CONTENT_URI, currentUri);

        /**
         * Set onClickListener on the cardView, when clicked open the detail view for given item
         * and pass the Uri in as Intent data.
         *
         * @param view the view the user has selected.
         */
        travelHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditorActivity.class);
                intent.setData(intentUri);
                context.startActivity(intent);
            }
        });
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
