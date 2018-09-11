package com.robynandcory.bonvoyage.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

public class TravelProvider extends ContentProvider {
    private TravelDbHelper mDbHelper;
    public static final String LOG_TAG = TravelProvider.class.getSimpleName();

    private static final int TRAVEL = 1;
    private static final int TRAVEL_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

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
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
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
                throw new IllegalArgumentException("Cannot process unkown URI: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAVEL:
                return insertTravel(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert data: " + uri);
        }
    }

    private Uri insertTravel (Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long rowId = database.insert(TravelContract.TravelEntry.TABLE_NAME, null, contentValues);
        if  (rowId == -1) {
            Log.e(LOG_TAG, "Insertion failed for: " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
