package com.robynandcory.bonvoyage;

import android.content.ContentValues;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.robynandcory.bonvoyage.data.TravelContract;

public class EditorActivity extends AppCompatActivity {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private Spinner mCategorySpinner;
    private int mCategory = TravelContract.TravelEntry.COLUMN_ITEM_CATEGORY_UNKNOWN;
    private Spinner mSeasonSpinner;
    private int mSeason = TravelContract.TravelEntry.COLUMN_ITEM_SEASON_ALLSEASON;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;

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
                Log.e(LOG_TAG, "this button was clicked");
                insertNewItem();

            }
        });

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

    private void insertNewItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
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

            Uri newUri = getContentResolver().insert(TravelContract.TravelEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this, "Error saving your item.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Your item has been added.", Toast.LENGTH_LONG).show();
            }

            Log.e(LOG_TAG, "This is what was entered" + contentValues);
            NavUtils.navigateUpFromSameTask(this);

        } catch (
                NumberFormatException e)

        {
            Toast.makeText(this, "Please enter a valid number",
                    Toast.LENGTH_LONG).show();
        }


    }

}
