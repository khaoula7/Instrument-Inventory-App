package com.example.android.musicinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.musicinventory.data.InventContract.*;

public class InventDbHelper extends SQLiteOpenHelper {
    /*Name of the database file*/
    public  static final int DATABASE_VERSION = 1;
    /*Database version. If you change the database schema you must increment the database version*/
    public static final String DATABASE_NAME = "musical.db";

    public InventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String that contains the SQL statement to create the instruments table
        String SQL_CREATE_INSTRUMENTS = "CREATE TABLE " + InstrumentEntry.TABLE_NAME + "( "
                + InstrumentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
                + InstrumentEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + InstrumentEntry.COLUMN_SERIAL + " TEXT NOT NULL, "
                + InstrumentEntry.COLUMN_BRAND + " TEXT NOT NULL, "
                + InstrumentEntry.COLUMN_PRICE + " DECIMAL NOT NULL, "
                + InstrumentEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, "
                + InstrumentEntry.COLUMN_NB + " INTEGER NOT NULL, "
                + " FOREIGN KEY( "+ InstrumentEntry.COLUMN_SUPPLIER_ID+" ) REFERENCES " + SupplierEntry._ID +"  )";

        //String that contains the SQL statement to create the instruments table
        String SQL_CREATE_SUPPLIERS = "CREATE TABLE " + SupplierEntry.TABLE_NAME + "( "
                + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SupplierEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + SupplierEntry.COLUMN_PHONE + " TEXT NOT NULL, "
                + SupplierEntry.COLUMN_EMAIL + " TEXT NOT NULL )";

        //Execute CREATE SQL STATEMENTS
        db.execSQL(SQL_CREATE_INSTRUMENTS);
        db.execSQL(SQL_CREATE_SUPPLIERS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + SupplierEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ InstrumentEntry.TABLE_NAME);
        // create new table
        onCreate(db);

    }
}
