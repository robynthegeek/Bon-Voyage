package com.robynandcory.bonvoyage.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

public class TravelProvider extends ContentProvider {
    private TravelDbHelper mDbHelper;

    //Log tag for error logging
    public static final String LOG_TAG = TravelProvider.class.getSimpleName();

    // URI matcher codes
    private static final int TRAVEL = 1;
    private static final int TRAVEL_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // URI paths for whole DB and single DB row.
    static {
        sUriMatcher.addURI(TravelContract.CONTENT_AUTHORITY, TravelContract.PATH_TRAVEL, TRAVEL);
        sUriMatcher.addURI(TravelContract.CONTENT_AUTHORITY, TravelContract.PATH_TRAVEL + "/#", TRAVEL_ID);

    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TravelDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // Matches the URI to either the full DB URI or a URI for a single row
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAVEL:
                cursor = database.query(
                        TravelContract.TravelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRAVEL_ID:
                selection = TravelEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        TravelContract.TravelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot process this URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        // Matches the URI to either the full DB URI or a URI for a single row

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAVEL:
                return TravelEntry.CONTENT_LIST_TYPE;
            case TRAVEL_ID:
                return TravelEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match type: " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        // Matches the URI to either the full DB URI or a URI for a single row
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAVEL:
                return insertTravel(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert data: " + uri);
        }
    }

    private Uri insertTravel(Uri uri, ContentValues contentValues) {
        /**
         * Throw error if provider receives a null value for the item name, price, quantity,
         * category, season, supplier name, and phone number.
         */
        String name = contentValues.getAsString(TravelEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Cannot create item without name");
        }
        Integer price = contentValues.getAsInteger(TravelEntry.COLUMN_PRICE);
        if (price == null || price < 0 || price > 99999) {
            throw new IllegalArgumentException("Price greater than 0 is required");
        }
        //Quantity limited to between 0 and 100 stock items.
        Integer quantity = contentValues.getAsInteger(TravelEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0 || quantity > 999) {
            throw new IllegalArgumentException("A valid quantity required");
        }
        Integer category = contentValues.getAsInteger(TravelEntry.COLUMN_CATEGORY);
        if (category == null || !TravelEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("A valid Category is required.");
        }
        Integer season = contentValues.getAsInteger(TravelEntry.COLUMN_SEASON);
        if (season == null || !TravelEntry.isValidSeason(season)) {
            throw new IllegalArgumentException("A valid Season is required.");
        }
        String supplierName = contentValues.getAsString(TravelEntry.COLUMN_SUPPLIER);
        if (supplierName == null) {
            throw new IllegalArgumentException("Cannot create item without Supplier");
        }
        String supplierPhone = contentValues.getAsString(TravelEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Cannot create item without Supplier Contact Phone");
        }
        // Write the sanity checked data to the DB
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long rowId = database.insert(TravelContract.TravelEntry.TABLE_NAME, null, contentValues);
        if (rowId == -1) {
            Log.e(LOG_TAG, "Insertion failed for: " + uri);
            return null;
        }
        // Notify listener of change and return new URI and row ID.
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        // Matches the URI to either the full DB URI or a URI for a single row
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAVEL:
                rowsDeleted = database.delete(TravelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAVEL_ID:
                selection = TravelEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TravelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete data: " + uri);
        }
        //Notify listener if rows have been deleted
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @NonNull ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        /**
         * Matches the URI to either the full DB URI or a URI for a single row,
         // uses updateTravel to update row or entire DB
         */
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAVEL:
                return updateTravel(uri, contentValues, selection, selectionArgs);
            case TRAVEL_ID:
                selection = TravelEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTravel(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot insert data: " + uri);
        }
    }
    /**
     * Updates the given row or entire DB for the input URI.
     * Check for the presence of each Key, then throw error if provider receives a null value
     * for the item name, price, quantity, category, season, supplier name, and phone number
     * only if they should be present.
     */
    public int updateTravel(@NonNull Uri uri, @NonNull ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        if (contentValues.containsKey(TravelEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(TravelEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Cannot create item without name");
            }
        }
        if (contentValues.containsKey(TravelEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(TravelEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Price greater than 0 is required");
            }
        }
        if (contentValues.containsKey(TravelEntry.COLUMN_QUANTITY)) {
            //Quantity limited to between 0 and 100 stock items.
            Integer quantity = contentValues.getAsInteger(TravelEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0 || quantity > 999) {
                throw new IllegalArgumentException("A valid quantity required");
            }
        }
        if (contentValues.containsKey(TravelEntry.COLUMN_CATEGORY)) {
            Integer category = contentValues.getAsInteger(TravelEntry.COLUMN_CATEGORY);
            if (category == null || !TravelEntry.isValidCategory(category)) {
                throw new IllegalArgumentException("A valid Category is required.");
            }
        }
        if (contentValues.containsKey(TravelEntry.COLUMN_SEASON)) {
            Integer season = contentValues.getAsInteger(TravelEntry.COLUMN_SEASON);
            if (season == null || !TravelEntry.isValidSeason(season)) {
                throw new IllegalArgumentException("A valid Season is required.");
            }
        }
        if (contentValues.containsKey(TravelEntry.COLUMN_SUPPLIER)) {
            String supplierName = contentValues.getAsString(TravelEntry.COLUMN_SUPPLIER);
            if (supplierName == null) {
                throw new IllegalArgumentException("Cannot create item without Supplier");
            }
        }
        if (contentValues.containsKey(TravelEntry.COLUMN_NAME)) {
            String supplierPhone = contentValues.getAsString(TravelEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Cannot create item without Supplier Contact Phone");
            }
        }

        //if the user has not actually updated any values, return 0 and do not change the database.
        if (contentValues.size() == 0) {
            return 0;
        }
        /**
         * Write the santity checked values to the DB only if new data has been entered.
         */
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated =  database.update(TravelEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
