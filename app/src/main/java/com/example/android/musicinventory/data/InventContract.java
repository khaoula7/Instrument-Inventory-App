package com.example.android.musicinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public final class InventContract {
    //Empty constructor to prevent someone from instantiating the class
    private InventContract(){}
    //
    public static final String CONTENT_AUTHORITY = "com.example.android.musicinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible Paths
    public static final String PATH_INSTRUMENTS ="instruments";
    public static final String PATH_SUPPLIERS = "suppliers";
    /**
     * Inner class that defines constant values for the instruments database table.
     * Each entry in the table represents a single instrument.
     */
    public static final class InstrumentEntry implements BaseColumns {
        //The content URI to access the instrument data in the provider
        public static final Uri CONTENT_INSTRUMENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INSTRUMENTS);
        //The MIME type of the {@link #CONTENT_INSTRUMENT_URI} for a list of instrument.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENTS;
        //The MIME type of the {@link #CONTENT_URI} for a single instrument.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENTS;
        // Define constants for Instrument table
        public static final String TABLE_NAME = "instruments";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERIAL = "serial";
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SUPPLIER_ID = "supplier_id";
        public static  final String COLUMN_NB = "nombre";
    }

    public static final class SupplierEntry implements BaseColumns {
        //The content URI to access the supplier data in the provider
        public static final Uri  CONTENT_SUPPLIER_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);
        //The MIME type of the {@link #CONTENT_SUPPLIER_URI} for a list of suppliers.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;
        //The MIME type of the {@link #CONTENT_URI} for a single pet.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;
        // Define constants for Supplier table
        public static final String TABLE_NAME = "suppliers";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";

    }

}
