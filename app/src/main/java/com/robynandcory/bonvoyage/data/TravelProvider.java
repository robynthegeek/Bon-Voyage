package com.robynandcory.bonvoyage.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TravelProvider extends ContentProvider{
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
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
