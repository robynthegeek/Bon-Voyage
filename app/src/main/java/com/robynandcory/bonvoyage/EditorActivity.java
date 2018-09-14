package com.robynandcory.bonvoyage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.robynandcory.bonvoyage.data.TravelContract;

public class EditorActivity extends AppCompatActivity {
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

}
