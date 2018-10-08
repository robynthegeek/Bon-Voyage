package com.robynandcory.bonvoyage;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.robynandcory.bonvoyage.data.TravelContract;
import com.robynandcory.bonvoyage.data.TravelContract.TravelEntry;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Credit for reference for phone number https://stackoverflow.com/questions/8196771/format-a-string-using-regex-in-java
 * https://stackoverflow.com/questions/30138159/check-sms-and-dial-support-before-intent
 */

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {


    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

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

        mNameEditText = findViewById(R.id.edit_item_name);
        mPriceEditText = findViewById(R.id.edit_item_price);
        mQuantityEditText = findViewById(R.id.edit_item_quantity);
        mCategorySpinner = findViewById(R.id.spinner_category);
        mSeasonSpinner = findViewById(R.id.spinner_season);
        mSupplierEditText = findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_item_supplier_phone);

        createSpinners();

        Button saveButton = findViewById(R.id.save_item_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItemData();
            }
        });

        Button reorderButton = findViewById(R.id.reorder_button);
        reorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
                if (itemSupplierPhone != null) {
                    dialerIntent.setData(Uri.parse("tel:" +itemSupplierPhone));
                    PackageManager packageManager = view.getContext().getPackageManager();
                    List activities = packageManager.queryIntentActivities(dialerIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    boolean dialerInstalled = activities.size() > 0;
                    if (dialerInstalled) {
                        startActivity(dialerIntent);
                    } else {
                        Toast.makeText(EditorActivity.this, "Please install a phone application", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        Intent editIntent = getIntent();
        mCurrentUri = editIntent.getData();
        if (mCurrentUri != null) {
            createEditor(mCurrentUri);
        } else {
            this.setTitle("Add an Item");
        }

    }

    private void createEditor(Uri uri) {
        this.setTitle("Edit Item");
        TextView editorTitle = findViewById(R.id.editor_text_title);
        editorTitle.setText("Edit Stock Item");
        getLoaderManager().initLoader(TRAVEL_ITEM_LOADER, null, this);


    }

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

    private void saveItemData() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().replaceAll("[^0-9]", "");
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();


        //check to see if any of the input strings are empty before saving.
        if (nameString.isEmpty() || priceString.isEmpty() || quantityString.isEmpty() ||
                supplierString.isEmpty() || supplierPhoneString.isEmpty()) {
            Toast.makeText(this, "Please fill all fields before saving.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //if all strings contain data, attempt to save the data.
        try {
            String priceStringCleaned = priceString.replaceAll("[^0-9]", "");
            int priceInteger = Integer.parseInt(priceStringCleaned);
            int quantityInteger = Integer.parseInt(quantityString);
            String supplierPhoneCleaned = supplierPhoneString.replaceAll("[^0-9]", "");

            if (supplierPhoneCleaned.length() != 10) {
                Toast.makeText(this, "Please enter a 10 digit US phone number.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (quantityString.length() > 3) {
                Toast.makeText(this, "Maximum stock quantity is 999.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (priceInteger > 99999) {
                Toast.makeText(this, "Maximum item price is $999.99.",
                        Toast.LENGTH_LONG).show();
                return;
            }

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
                    Toast.makeText(this, "Error saving your item.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Your item has been added.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Uri newUri = getContentResolver().insert(TravelContract.TravelEntry.CONTENT_URI, contentValues);
                if (newUri == null) {
                    Toast.makeText(this, "Error saving your item.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Your item has been added.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            Log.e(LOG_TAG, "This is what was entered" + contentValues);
            // NavUtils.navigateUpFromSameTask(this);

        } catch (
                NumberFormatException e)

        {
            Toast.makeText(this, "Please enter a valid number",
                    Toast.LENGTH_LONG).show();
        }


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
            String itemPrice = cursor.getString(priceColumnIndex);
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
                case TravelEntry.COLUMN_ITEM_CATEGORY_CLOTHING:
                    mCategorySpinner.setSelection(2);
                case TravelEntry.COLUMN_ITEM_CATEGORY_TOILETRIES:
                    mCategorySpinner.setSelection(3);
                case TravelEntry.COLUMN_ITEM_CATEGORY_ACCESSORIES:
                    mCategorySpinner.setSelection(4);
                case TravelEntry.COLUMN_ITEM_CATEGORY_LUGGAGE:
                    mCategorySpinner.setSelection(5);
                default:
                    mCategorySpinner.setSelection(0);
                    break;
            }
            switch (itemSeason) {
                case TravelEntry.COLUMN_ITEM_SEASON_COLD:
                    mSeasonSpinner.setSelection(1);
                case TravelEntry.COLUMN_ITEM_SEASON_HOT:
                    mSeasonSpinner.setSelection(2);
                default:
                    mSeasonSpinner.setSelection(0);
                    break;
            }
        }
    }

    private String formatPhoneNumber (String itemSupplierPhone){
        StringBuilder stringBuilder = new StringBuilder(itemSupplierPhone)
                .insert(0,"(")
                .insert(4,")")
                .insert(8,"-");
        String formattedPhoneNumber = stringBuilder.toString();
        return formattedPhoneNumber;
    }

    private String formatPrice (String intPrice) {
        return intPrice.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mCategorySpinner.setSelection(0); //Unknown Category
        mSeasonSpinner.setSelection(0);  //Allseason

    }
}
