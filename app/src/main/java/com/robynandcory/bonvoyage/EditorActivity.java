package com.robynandcory.bonvoyage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Spinner;

public class EditorActivity extends AppCompatActivity {
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private Spinner mCategorySpinner;
    private Spinner mSeasonSpinner;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_layout);

        mNameEditText = findViewById(R.id.edit_item_name);
        mPriceEditText = findViewById(R.id.edit_item_price);
        mQuantityEditText = findViewById(R.id.edit_item_quantity);
        mCategorySpinner = findViewById(R.id.spinner_category);
        mSeasonSpinner = findViewById(R.id.spinner_season);
        mSupplierEditText = findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_item_supplier_phone);

    }
}
