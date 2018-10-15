package com.robynandcory.bonvoyage;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.robynandcory.bonvoyage.data.TravelContract;
import com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

import java.util.List;
import java.util.Locale;


/**
 * Allows user to view and edit existing items, and add new items depending on whether the
 * Add item or Edit button is clicked.
 * Credit for reference for phone number:
 * https://stackoverflow.com/questions/8196771/format-a-string-using-regex-in-java
 * https://stackoverflow.com/questions/30138159/check-sms-and-dial-support-before-intent
 */

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    // Version for Loader
    private static final int TRAVEL_ITEM_LOADER = 1;

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private Spinner mCategorySpinner;
    private int mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_UNKNOWN;
    private Spinner mSeasonSpinner;
    private int mSeason = TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private Uri mCurrentUri;
    private String itemSupplierPhone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_layout);

        //Locate all item views
        mNameEditText = findViewById(R.id.edit_item_name);
        mPriceEditText = findViewById(R.id.edit_item_price);
        mQuantityEditText = findViewById(R.id.edit_item_quantity);
        mCategorySpinner = findViewById(R.id.spinner_category);
        mSeasonSpinner = findViewById(R.id.spinner_season);
        mSupplierEditText = findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_item_supplier_phone);

        createSpinners();

        //set ClickListener on Save button
        Button saveButton = findViewById(R.id.save_item_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItemData();
            }
        });

        // If the user was referred by an intent, set the title to match Editor mode and add the
        // views for editing, otherwise, use the default title.
        Intent editIntent = getIntent();
        mCurrentUri = editIntent.getData();
        if (mCurrentUri != null) {
            createEditor(mCurrentUri);
        } else {
            this.setTitle(getString(R.string.add_item));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks, allows user to delete single item record from database
        switch (item.getItemId()) {
            case R.id.action_delete_entry:
                deleteItemEntry();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * If user is sent to EditorActivity by an intent, extract the URI and build Editor mode
     *
     * @param uri of the item the user has selected.
     */
    private void createEditor(Uri uri) {
        this.setTitle(getString(R.string.edit_item));
        TextView editorTitle = findViewById(R.id.editor_text_title);
        editorTitle.setText(R.string.edit_stock_item);
        getLoaderManager().initLoader(TRAVEL_ITEM_LOADER, null, this);

        // Locate Reorder button and set up intent to dial phone number of supplier
        Button reorderButton = findViewById(R.id.reorder_button);
        reorderButton.setVisibility(View.VISIBLE);
        reorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
                if (itemSupplierPhone != null) {
                    dialerIntent.setData(Uri.parse("tel:" + itemSupplierPhone));
                    PackageManager packageManager = view.getContext().getPackageManager();
                    List activities = packageManager.queryIntentActivities(dialerIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    boolean dialerInstalled = activities.size() > 0;
                    if (dialerInstalled) {
                        startActivity(dialerIntent);
                    } else {
                        // If user does not have a dialer installed, prompt them to install one
                        // to use reorder functionality.
                        Toast.makeText(EditorActivity.this, R.string.install_phone_application, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        /**
         * Creates button to decrease stock amount by one unless amount is 0
         */
        Button decrementButton = findViewById(R.id.decrement_button);
        decrementButton.setVisibility(View.VISIBLE);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityInt = Integer.valueOf(mQuantityEditText.getText().toString());
                if (quantityInt > 0 && mCurrentUri != null) {
                    quantityInt -= 1;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TravelEntry.COLUMN_QUANTITY, quantityInt);
                    int rowsUpdated = getContentResolver().update(mCurrentUri,
                            contentValues,
                            null,
                            null);
                    if (rowsUpdated == 0) {
                        Toast.makeText(EditorActivity.this, R.string.error_saving, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EditorActivity.this, R.string.cannot_set_negative_amount, Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Creates button to increase stock amount by one unless amount is 0
         */
        Button incrementButton = findViewById(R.id.increment_button);
        incrementButton.setVisibility(View.VISIBLE);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityInt = Integer.valueOf(mQuantityEditText.getText().toString());
                if (quantityInt < 999 && mCurrentUri != null) {
                    quantityInt += 1;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TravelEntry.COLUMN_QUANTITY, quantityInt);
                    int rowsUpdated = getContentResolver().update(mCurrentUri,
                            contentValues,
                            null,
                            null);
                    if (rowsUpdated == 0) {
                        Toast.makeText(EditorActivity.this, R.string.error_saving, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EditorActivity.this, R.string.cannot_set_amount_over_999, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Creates the category and season spinners
     */
    private void createSpinners() {
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);

        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCategorySpinner.setAdapter(categorySpinnerAdapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> categoryAdapterView, View view, int i, long l) {
                String selection = (String) categoryAdapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_unknown))) {
                        mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_UNKNOWN;
                    } else if (selection.equals(getString(R.string.category_technology))) {
                        mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_TECHNOLOGY;
                    } else if (selection.equals(getString(R.string.category_clothing))) {
                        mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_CLOTHING;
                    } else if (selection.equals(getString(R.string.category_toiletries))) {
                        mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_TOILETRIES;
                    } else if (selection.equals(getString(R.string.category_accessories))) {
                        mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_ACCESSORIES;
                    } else if (selection.equals(getString(R.string.category_luggage))) {
                        mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_LUGGAGE;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> categoryAdapterView) {
                mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_UNKNOWN;
            }
        });

        ArrayAdapter seasonSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_season_options, android.R.layout.simple_spinner_item);

        seasonSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSeasonSpinner.setAdapter(seasonSpinnerAdapter);
        mSeasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> seasonAdapterView, View view, int i, long l) {
                String selection = (String) seasonAdapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.all_season))) {
                        mSeason = TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON;
                    } else if (selection.equals(getString(R.string.cold_weather))) {
                        mSeason = TravelContract.TravelEntry.COLUMN_ITEM_SEASON_COLD;
                    } else if (selection.equals(getString(R.string.hot_weather))) {
                        mSeason = TravelContract.TravelEntry.COLUMN_ITEM_SEASON_HOT;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> seasonAdapterView) {
                mSeason = TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON;
            }
        });
    }

    /**
     * Retrieves the user-entered Data from the EditText fields and spinners, checks for empty
     * fields, and sanity checks entered data.  If data is acceptable, writes to DB using
     * Content Resolver.
     */
    private void saveItemData() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().replaceAll("[^0-9]", "");
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();


        //check to see if any of the input strings are empty before saving.
        if (nameString.isEmpty() || priceString.isEmpty() || quantityString.isEmpty() ||
                supplierString.isEmpty() || supplierPhoneString.isEmpty()) {
            Toast.makeText(this, R.string.fill_before_saving,
                    Toast.LENGTH_LONG).show();
            return;
        }

        //if all strings contain data, attempt to save the data.
        try {
            //Clean price and phone strings of added symbols, leaving digits.
            String priceStringCleaned = priceString.replaceAll("[^0-9]", "");
            int priceInteger = Integer.parseInt(priceStringCleaned);
            int quantityInteger = Integer.parseInt(quantityString);
            String supplierPhoneCleaned = supplierPhoneString.replaceAll("[^0-9]", "");

            // Check for standard US phone length.
            if (supplierPhoneCleaned.length() != 10) {
                Toast.makeText(this, R.string.enter_us_phone,
                        Toast.LENGTH_LONG).show();
                return;
            }
            //Ensure maximum stock quantity does not exceed 999
            if (quantityString.length() > 3) {
                Toast.makeText(this, R.string.maximum_stock_quantity,
                        Toast.LENGTH_LONG).show();
                return;
            }
            //Ensure maximum price does not exceed $999.99 (99999 cents)
            if (priceInteger > 99999) {
                Toast.makeText(this, R.string.maximum_price,
                        Toast.LENGTH_LONG).show();
                return;
            }

            //Add sanity checked values to the ContentValues
            ContentValues contentValues = new ContentValues();
            contentValues.put(TravelContract.TravelEntry.COLUMN_NAME, nameString);
            contentValues.put(TravelContract.TravelEntry.COLUMN_PRICE, priceInteger);
            contentValues.put(TravelContract.TravelEntry.COLUMN_QUANTITY, quantityInteger);
            contentValues.put(TravelContract.TravelEntry.COLUMN_CATEGORY, mCategory);
            contentValues.put(TravelContract.TravelEntry.COLUMN_SEASON, mSeason);
            contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER, supplierString);
            contentValues.put(TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneCleaned);

            if (mCurrentUri != null) {
                int rowsUpdated = getContentResolver().update(mCurrentUri,
                        contentValues,
                        null,
                        null);
                if (rowsUpdated == 0) {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Uri newUri = getContentResolver().insert(TravelContract.TravelEntry.CONTENT_URI, contentValues);
                if (newUri == null) {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            // If user entered numbers are malformed, show a toast to alert them.
        } catch (
                NumberFormatException e) {
            Toast.makeText(this, R.string.enter_valid_number,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Allows user to delete all entries from the settings menu.
     */
    private void deleteItemEntry() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.confirm_deletion);
        alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rowsDeleted = getContentResolver().delete(mCurrentUri,
                        null, null);
                if (rowsDeleted > 0) {
                    Toast.makeText(EditorActivity.this, R.string.deletion_confirmed, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(EditorActivity.this, R.string.deletion_failed, Toast.LENGTH_SHORT).show();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TravelContract.TravelEntry._ID,
                TravelContract.TravelEntry.COLUMN_NAME,
                TravelContract.TravelEntry.COLUMN_PRICE,
                TravelContract.TravelEntry.COLUMN_QUANTITY,
                TravelContract.TravelEntry.COLUMN_CATEGORY,
                TravelContract.TravelEntry.COLUMN_SEASON,
                TravelContract.TravelEntry.COLUMN_SUPPLIER,
                TravelContract.TravelEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this,
                mCurrentUri,  //Uri for the item user is editing
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_CATEGORY);
            int seasonColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_SEASON);
            int supplierColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(TravelEntry.COLUMN_SUPPLIER_PHONE);


            String itemName = cursor.getString(nameColumnIndex);
            int itemPrice = cursor.getInt(priceColumnIndex);
            int itemQuantity = cursor.getInt(quantityColumnIndex);
            int itemCategory = cursor.getInt(categoryColumnIndex);
            int itemSeason = cursor.getInt(seasonColumnIndex);
            String itemSupplier = cursor.getString(supplierColumnIndex);
            itemSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mNameEditText.setText(itemName);
            mPriceEditText.setText(formatPrice(itemPrice));
            mQuantityEditText.setText(Integer.toString(itemQuantity));
            mSupplierEditText.setText(itemSupplier);
            mSupplierPhoneEditText.setText(formatPhoneNumber(itemSupplierPhone));
            switch (itemCategory) {
                case TravelEntry.COLUMN_ITEM_CATEGORY_TECHNOLOGY:
                    mCategorySpinner.setSelection(1);
                    break;
                case TravelEntry.COLUMN_ITEM_CATEGORY_CLOTHING:
                    mCategorySpinner.setSelection(2);
                    break;
                case TravelEntry.COLUMN_ITEM_CATEGORY_TOILETRIES:
                    mCategorySpinner.setSelection(3);
                    break;
                case TravelEntry.COLUMN_ITEM_CATEGORY_ACCESSORIES:
                    mCategorySpinner.setSelection(4);
                    break;
                case TravelEntry.COLUMN_ITEM_CATEGORY_LUGGAGE:
                    mCategorySpinner.setSelection(5);
                    break;
                default:
                    mCategorySpinner.setSelection(0);
                    break;
            }
            switch (itemSeason) {
                case TravelEntry.COLUMN_ITEM_SEASON_COLD:
                    mSeasonSpinner.setSelection(1);
                    break;
                case TravelEntry.COLUMN_ITEM_SEASON_HOT:
                    mSeasonSpinner.setSelection(2);
                    break;
                default:
                    mSeasonSpinner.setSelection(0);
                    break;
            }
        }
    }

    /**
     * Formats the phone number in format 6505551234 to (650) 555-1234
     *
     * @param itemSupplierPhone cleaned string from database
     * @return correctly formatted phone number String
     */
    private String formatPhoneNumber(String itemSupplierPhone) {
        StringBuilder stringBuilder = new StringBuilder(itemSupplierPhone)
                .insert(0, "(")
                .insert(4, ")")
                .insert(8, "-");
        return stringBuilder.toString();
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //when loader resets, clear all fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mCategorySpinner.setSelection(0); //Unknown Category
        mSeasonSpinner.setSelection(0);  //Allseason

    }
}
