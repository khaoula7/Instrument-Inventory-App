package com.example.android.musicinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.function.Supplier;

import static com.example.android.musicinventory.data.InventContract.*;


public class InventProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = InventProvider.class.getSimpleName();
    /**Database Helper object*/
    private InventDbHelper mInventDbHelper;
    /** URI matcher code for the content URI for the suppliers table */
    private  static  final int SUPPLIERS = 100;
    /** URI matcher code for the content URI for a single supplier in the suppliers table */
    private static final int SUPPLIER_ID = 101;
    /** URI matcher code for the content URI for the instruments table */
    private  static  final int INSTRUMENTS = 200;
    /** URI matcher code for the content URI for a single instrument in the suppliers table */
    private static final int INSTRUMENT_ID = 201;

    /** UriMatcher object to match suppliers/instruments content URI to a corresponding code.*/
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Static initializer. This is run the first time anything is called from this class.*/
    static{
        //Add all of the content URI patterns that the provider should recognize.
        // All paths added to the UriMatcher have a corresponding code to return when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_SUPPLIERS, SUPPLIERS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_SUPPLIERS + "/#", SUPPLIER_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INSTRUMENTS, INSTRUMENTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INSTRUMENTS + "/#", INSTRUMENT_ID);
    }

    /**
     * Initialize the provider and the database helper object to gain access to the database.
     *
     */
    @Override
    public boolean onCreate() {
        mInventDbHelper = new InventDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mInventDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match){
            case SUPPLIERS: //Perform database query on SUPPLIERS table
                cursor= database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                // Extract the ID from the URI. selection will be "_id=?"
                selection = SupplierEntry._ID + "=?";
                // selectionArgs will be a String array containing the actual ID
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INSTRUMENTS:
                cursor=database.query(InstrumentEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INSTRUMENT_ID:
                selection = InstrumentEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InstrumentEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Specify the uri to watchIf the data at this URI changes, then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case SUPPLIERS:
                return insertSupplier(uri, values);
            case INSTRUMENTS:
                return insertInstrument(uri, values);
            default:throw new IllegalArgumentException("insertion is not supported for this URI: "+ uri);
        }
    }

    private Uri insertSupplier(Uri uri, ContentValues values) {
        //Make sure values fields are not empty
        String name = values.getAsString(SupplierEntry.COLUMN_NAME);
        String tel = values.getAsString(SupplierEntry.COLUMN_PHONE);
        String email = values.getAsString(SupplierEntry.COLUMN_EMAIL);
        if(name == null) throw new IllegalArgumentException("Supplier requires a name");
        if(tel == null) throw new IllegalArgumentException("Supplier requires a phone number");
        if(email == null) throw new IllegalArgumentException("Supplier requires an email adress");

        //Get database open in writable mode
        SQLiteDatabase db = mInventDbHelper.getWritableDatabase();
        long id = db.insert(SupplierEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertInstrument(Uri uri, ContentValues values) {
        //Check data sanity
        String instrument = values.getAsString(InstrumentEntry.COLUMN_NAME);
        if(instrument == null){
            throw new IllegalArgumentException("Instrument requires a name");
        }
        String brand = values.getAsString(InstrumentEntry.COLUMN_BRAND);
        if(brand == null){
            throw new IllegalArgumentException("Instrument requires a brand");
        }
        String serial = values.getAsString(InstrumentEntry.COLUMN_SERIAL);
        if(serial == null){
            throw new IllegalArgumentException("Instrument requires a Serial");
        }
        Float price = values.getAsFloat(InstrumentEntry.COLUMN_PRICE);
        if(price == 0){
            throw new IllegalArgumentException("Instrument requires a price");
        }
        int quantity = values.getAsInteger(InstrumentEntry.COLUMN_NB);
        if(quantity == 0){
            throw new IllegalArgumentException("Instrument requires a quantity");
        }
        int idSupplier = values.getAsInteger(InstrumentEntry.COLUMN_SUPPLIER_ID);
        if(idSupplier == 0)
            Log.i(LOG_TAG, "insertInstrument: Instrument has no valid supplier");
        //Open database in writable mode
        SQLiteDatabase db = mInventDbHelper.getWritableDatabase();
        long id = db.insert(InstrumentEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has changed for the pet content URI (for CursorLoader to automatically refresh ListView)
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mInventDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (match){
            case INSTRUMENTS:
                rowsDeleted = database.delete(InstrumentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INSTRUMENT_ID:
                selection = InstrumentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
               rowsDeleted = database.delete(InstrumentEntry.TABLE_NAME, selection, selectionArgs);
               break;
               default:
                    throw new IllegalArgumentException("Delete is not supported for : "+uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case INSTRUMENTS:
                return updateQuantity(uri, values, selection, selectionArgs);
            case INSTRUMENT_ID:
                selection = InstrumentEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateQuantity(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

   private int updateQuantity(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if(values.size() == 0)
            return 0;
        if(values.containsKey(InstrumentEntry.COLUMN_NB)){
            Integer quantity =  values.getAsInteger(InstrumentEntry.COLUMN_NB);
        }
        SQLiteDatabase database = mInventDbHelper.getWritableDatabase();
        int rows = database.update(InstrumentEntry.TABLE_NAME, values, selection, selectionArgs);
        if( rows != 0){
            //Notify all listeners that the data at the given Uri has changed.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri); //match is the code of the content URI
        switch(match){
            case SUPPLIERS:
                return SupplierEntry.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;
            case INSTRUMENTS:
                return InstrumentEntry.CONTENT_LIST_TYPE;
            case INSTRUMENT_ID:
                return InstrumentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
