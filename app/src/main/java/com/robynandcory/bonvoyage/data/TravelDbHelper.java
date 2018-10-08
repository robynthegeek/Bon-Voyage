package com.robynandcory.bonvoyage.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

public class TravelDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TravelDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "BonVoyage.db";

    public TravelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TRAVEL_DB = "CREATE TABLE " + TravelContract.TravelEntry.TABLE_NAME + " ("
                + TravelContract.TravelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TravelContract.TravelEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + TravelContract.TravelEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + TravelContract.TravelEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + TravelContract.TravelEntry.COLUMN_CATEGORY + " INTEGER NOT NULL DEFAULT 0, "
                + TravelContract.TravelEntry.COLUMN_SEASON + " INTEGER NOT NULL DEFAULT 0, "
                + TravelContract.TravelEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_TRAVEL_DB);
        Log.e(LOG_TAG, "DB Contains: " + SQL_CREATE_TRAVEL_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

        }
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TravelContract.TravelEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
    }
}
