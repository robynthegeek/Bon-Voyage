package com.robynandcory.bonvoyage;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.robynandcory.bonvoyage.data.TravelContract;
import com.robynandcory.bonvoyage.data.TravelDbHelper;


/**
 * Project 8 for Udacity ABND
 * <p>
 * Current app contains a single SQLite table to store inventory information for a travel store.
 * <p>
 * References:
 * https://github.com/udacity/ud845-Pets
 * https://stackoverflow.com/questions/28217436/how-to-show-an-empty-view-with-a-recyclerview
 * Icons paid use from https://gumroad.com/d/302e27c9605ad25705945f65e006e1a4
 */

public class TravelCatalogMain extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    //Database helper and Cursor Adapter
    private TravelDbHelper mDbHelper;
    TravelCursorAdapter mCursorAdapter;

    //Views for the item list
    RecyclerView recyclerView;
    ConstraintLayout emptyConstraintLayout;

    //Item loader ID
    private static final int LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up main layout and toolbar
        setContentView(R.layout.activity_travel_catalog_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add button for adding new items to DB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TravelCatalogMain.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayTravelDb();
    }


    /**
     * Inserts 3 rows test data to the Travel Database
     */
    private void testWriteTravelDB() {
        mDbHelper = new TravelDbHelper(this);
        SQLiteDatabase wDb = mDbHelper.getWritableDatabase();

        // Adds 3 rows of test data
        ContentValues contentValues = new ContentValues();
        contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, "Travel Bag");
        contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, 12599);
        contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, 5);
        contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_LUGGAGE);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, "Osprey");
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, "5035551234");

        Uri newUri = getContentResolver().insert(TravelContract.TravelEntry.CONTENT_URI, contentValues);

        contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, "Travel Shampoo");
        contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, 299);
        contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, 8);
        contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_TOILETRIES);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, "Suave");
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, "5035551384");

        Uri newUri2 = getContentResolver().insert(TravelContract.TravelEntry.CONTENT_URI, contentValues);

        contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, "IceBreaker T-Shirt");
        contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, 4599);
        contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, 9);
        contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_CLOTHING);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, TravelContract.TravelEntry.COLUMN_ITEM_SEASON_COLD);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, "IceBreaker");
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, "5035551384");

        Uri newUri3 = getContentResolver().insert(TravelContract.TravelEntry.CONTENT_URI, contentValues);
    }

    /**
     * Reads the rows of test data and displays the results on the screen.
     * For grading and debugging purposes only.
     */
    private void displayTravelDb() {

        //locate recyclerView for list items
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //sets up recyclerView with the cursorAdapter
        mCursorAdapter = new TravelCursorAdapter(this, null);
        mCursorAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //View for the empty item list
                emptyConstraintLayout = findViewById(R.id.empty_list_layout);

                if (mCursorAdapter.getItemCount() == 0) {
                    emptyConstraintLayout.setVisibility(View.VISIBLE);
                } else {
                    emptyConstraintLayout.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(LOADER, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_travel_catalog_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks, allows 2 options: insert a test group of items,
        // and delete all of the Database entries.
        switch (item.getItemId()) {
            case R.id.action_insert_test_set:
                testWriteTravelDB();
                displayTravelDb();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Allows user to delete all entries from the settings menu.
     * Prompts for confirmation first.
     */
    private void deleteAllEntries() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.delete_all_data_confirm);
        alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rowsDeleted = getContentResolver().delete(TravelContract.TravelEntry.CONTENT_URI,
                        null, null);
                if (rowsDeleted > 0) {
                    Toast.makeText(TravelCatalogMain.this, R.string.deletion_confirmed, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TravelCatalogMain.this, R.string.deletion_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Show a confirmation dialogue before deleting all database items.
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        (alertDialogBuilder.create()).show();
    }

    /**
     * @param id   for the row in question
     * @param args not currently used
     * @return returns the a new Cursor Loader for travel items.
     */
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TravelContract.TravelEntry._ID,
                TravelContract.TravelEntry.COLUMN_NAME,
                TravelContract.TravelEntry.COLUMN_PRICE,
                TravelContract.TravelEntry.COLUMN_QUANTITY
        };
        return new CursorLoader(this,
                TravelContract.TravelEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
