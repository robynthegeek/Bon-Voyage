package com.robynandcory.bonvoyage;

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

import com.robynandcory.bonvoyage.data.TravelContract;
import com.robynandcory.bonvoyage.data.TravelDbHelper;


/**
 * Icons paid use from https://gumroad.com/d/302e27c9605ad25705945f65e006e1a4
 */
public class TravelCatalogMain extends AppCompatActivity {

    private TravelDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_catalog_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDbHelper = new TravelDbHelper(this);

    }

    private void testTravelDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TravelContract.TravelEntry._ID,
                TravelContract.TravelEntry.COLUMN_NAME,
                TravelContract.TravelEntry.COLUMN_PRICE,
                TravelContract.TravelEntry.COLUMN_QUANTITY,
                TravelContract.TravelEntry.COLUMN_SUPPLIER,
                TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE,
        };
        Cursor cursor = db.query(
                TravelContract.TravelEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }

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
