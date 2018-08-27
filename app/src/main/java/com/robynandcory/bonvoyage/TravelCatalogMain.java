package com.robynandcory.bonvoyage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.robynandcory.bonvoyage.data.TravelContract;
import com.robynandcory.bonvoyage.data.TravelDbHelper;


/**
 * Project 8 for Udacity ABND
 *
 * Current app contains a single SQLite table to store inventory information for a travel store.
 * UI to be completed in phase 2 of the project.
 *
 * Icons paid use from https://gumroad.com/d/302e27c9605ad25705945f65e006e1a4
 */

public class TravelCatalogMain extends AppCompatActivity {

    private TravelDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_catalog_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add button per material guidelines for new inventory items.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "During UI phase this will add an entry", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //Inserts single entry to test DB, for debugging and grading only.
        testWriteTravelDB();
        //reads from DB and displays in textView on the screen.  For debugging and grading only.
        testReadTravelDB();
    }

    /**
     * Inserts 3 rows test data to the Travel Database
     * For grading and debugging purposes only.
     */
    private void testWriteTravelDB() {
        mDbHelper = new TravelDbHelper(this);
        SQLiteDatabase wDB = mDbHelper.getWritableDatabase();

        // Clear DB by deleting all entries before adding new test data
        wDB.delete(TravelContract.TravelEntry.TABLE_NAME, null, null);

        // Adds 3 rows of test data
        ContentValues contentValues = new ContentValues();
        contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, "Travel Backpack");
        contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, 125.99);
        contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, 5);
        contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_LUGGAGE);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, "Osprey");
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, "503.555.1234");

        wDB.insert(TravelContract.TravelEntry.TABLE_NAME, null, contentValues);

        contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, "Travel Shampoo");
        contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, 2.99);
        contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, 8);
        contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_TOILETRIES);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, "Suave");
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, "503.555.1384");

        wDB.insert(TravelContract.TravelEntry.TABLE_NAME, null, contentValues);

        contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, "IceBreaker T-Shirt");
        contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, 45.99);
        contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, 9);
        contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_CLOTHING);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, TravelContract.TravelEntry.COLUMN_ITEM_SEASON_COLD);
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, "IceBreaker");
        contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, "503.555.1384");

        wDB.insert(TravelContract.TravelEntry.TABLE_NAME, null, contentValues);
    }

    /**
     * Reads the rows of test data and displays the results on the screen.
     * For grading and debugging purposes only.
     */
    private void testReadTravelDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TravelContract.TravelEntry._ID,
                TravelContract.TravelEntry.COLUMN_NAME,
                TravelContract.TravelEntry.COLUMN_PRICE,
                TravelContract.TravelEntry.COLUMN_QUANTITY,
                TravelContract.TravelEntry.COLUMN_CATEGORY,
                TravelContract.TravelEntry.COLUMN_SEASON,
                TravelContract.TravelEntry.COLUMN_SUPPLIER,
                TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE,
        };

        // Query selects all results from current inventory of test data
        Cursor cursor = db.query(
                TravelContract.TravelEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        //Locate test textViews
        TextView rowCountView = findViewById(R.id.row_count);
        TextView testQueryView = findViewById(R.id.test_query);


        try {
            rowCountView.setText("There are currently " + cursor.getCount() + " items in your inventory.");
            testQueryView.setText("Inventory Contains: \n");
            testQueryView.append(TravelContract.TravelEntry._ID + " - " +
                    TravelContract.TravelEntry.COLUMN_NAME + " - " +
                    TravelContract.TravelEntry.COLUMN_PRICE + " - " +
                    TravelContract.TravelEntry.COLUMN_QUANTITY + " - " +
                    TravelContract.TravelEntry.COLUMN_CATEGORY + " - " +
                    TravelContract.TravelEntry.COLUMN_SEASON + " - " +
                    TravelContract.TravelEntry.COLUMN_SUPPLIER + " - " +
                    TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE + "\n");

            int idColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_CATEGORY);
            int seasonColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_SEASON);
            int supplierColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                long currentPrice = cursor.getLong(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentCategory = cursor.getInt(categoryColumnIndex);
                int currentSeason = cursor.getInt(seasonColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
                testQueryView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentCategory + " - " +
                        currentSeason + " - " +
                        currentSupplier + " - " +
                        currentSupplierPhone));
            }

        } finally {
            cursor.close();
        }
    }

//ToDo Add support for menu options in the UI phase.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_travel_catalog_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
