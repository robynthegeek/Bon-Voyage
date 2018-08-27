package com.robynandcory.bonvoyage.data;


import android.provider.BaseColumns;

/**
 * API Contract for Travel Item DB
 */
public class TravelContract {
    private TravelContract() {
    }

    public static final class TravelEntry implements BaseColumns {

        public final static String TABLE_NAME = "travel";

        /**
         * UUID integer for travel items, auto increments without duplication.
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Item title stored as a String
         */
        public final static String COLUMN_NAME = "name";

        /**
         * Cost of the item, stored as Real
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of the item stored as an integer
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Item category, such as outdoor, luggage, or technology, stored as integer
         */
        public final static String COLUMN_CATEGORY = "category";

        /**
         * possible values for category
         */
        public static final int COLUMN_ITEM_CATEGORY_UNKNOWN = 0;
        public static final int COLUMN_ITEM_CATEGORY_TECHNOLOGY = 1;
        public static final int COLUMN_ITEM_CATEGORY_CLOTHING = 2;
        public static final int COLUMN_ITEM_CATEGORY_TOILETRIES = 3;
        public static final int COLUMN_ITEM_CATEGORY_ACCESSORIES = 4;
        public static final int COLUMN_ITEM_CATEGORY_LUGGAGE = 5;
        //public static final int DOCUMENTS = ?;
        //public static final int OUTDOOR = ?;

        /**
         * Season for item sale.
         */
        public final static String COLUMN_SEASON = "season";

        /**
         * Booleans represented as integers indicating item seasonality by weather.
         */
        public final static int COLUMN_ITEM_SEASON_ALLSEASON = 0;
        public final static int COLUMN_ITEM_SEASON_COLD = 1;
        public final static int COLUMN_ITEM_SEASON_WARM = 2;


        /**
         * Supplier name stored as string
         */
        public final static String COLUMN_SUPPLIER = "supplier";

        /**
         * Supplier name stored as string
         */
        public final static String COLUMN_SUPPLIER_PHONE = "phone";

    }
}
