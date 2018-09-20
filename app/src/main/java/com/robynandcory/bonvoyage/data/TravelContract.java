package com.robynandcory.bonvoyage.data;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for Travel Item DB
 */
public class TravelContract {
    private TravelContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.robynandcory.bonvoyage";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TRAVEL = "travel";


    public static final class TravelEntry implements BaseColumns {
        /**
         * Content URI for accessing travel data via the content provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRAVEL);

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
        public static final int CATEGORY_MAX = 5;

        //check for valid category  and allow categories to expand if new item categories are added.
        public static boolean isValidCategory(int category) {
            if (category <= CATEGORY_MAX && category >= 0) {
                return true;
            }
            return false;
        }

        /**
         * Season for item sale.
         */
        public final static String COLUMN_SEASON = "season";

        /**
         * Booleans represented as integers indicating item seasonality by weather.
         */
        public final static int COLUMN_ITEM_SEASON_ALLSEASON = 0;
        public final static int COLUMN_ITEM_SEASON_COLD = 1;
        public final static int COLUMN_ITEM_SEASON_HOT = 2;

        public static boolean isValidSeason(int season) {
            if (season == COLUMN_ITEM_SEASON_ALLSEASON || season == COLUMN_ITEM_SEASON_COLD
                    || season == COLUMN_ITEM_SEASON_HOT) {
                return true;
            }
            return false;
        }


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
