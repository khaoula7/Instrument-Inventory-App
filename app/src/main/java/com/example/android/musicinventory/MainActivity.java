package com.example.android.musicinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.musicinventory.data.InventContract;
import com.example.android.musicinventory.data.InventDbHelper;

import java.util.ArrayList;

import static com.example.android.musicinventory.data.InventContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int inventLoader = 0;
    InventCursorAdapter cursorAdapter;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Add Listener to FAB to open Editor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        //Find ListView which will be populated by instruments
        ListView listView = (ListView) findViewById(R.id.list_view);
        //Find and set empty view on the ListView, so that it only shows when the ListView has 0 items
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //There is no pet data yet (until the loader finishes) so pass in null for the cursor.
        cursorAdapter = new InventCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =  new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentUri = ContentUris.withAppendedId(InstrumentEntry.CONTENT_INSTRUMENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });


        //Initialize and activate loader
        getLoaderManager().initLoader(inventLoader, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the projection we care about.
        String [] projection = {InstrumentEntry._ID,
                InstrumentEntry.COLUMN_NAME,
                InstrumentEntry.COLUMN_SERIAL,
                InstrumentEntry.COLUMN_NB,
                InstrumentEntry.COLUMN_PRICE};
        return new CursorLoader(this, InstrumentEntry.CONTENT_INSTRUMENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update PetCursorAdapter with this new cursor containing updated pet data
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted
        cursorAdapter.swapCursor(null);
    }












    /**Helper method to display info in database using raw SQL. For testing purposes only.

     private void displayDataBaseInfo() {
     //Instanciate our subclass of OpenSQliteHelper to Access database
     InventDbHelper mDbHelper = new InventDbHelper(this);
     // Create and/or open a database to read from it
     SQLiteDatabase db  = mDbHelper.getReadableDatabase();
     // Perform this raw SQL query "SELECT * FROM suppliers" to get a Cursor that contains all rows from the pets table.
     Cursor cursor = db.rawQuery("SELECT * FROM " + SupplierEntry.TABLE_NAME, null);

     TextView displayTextView = (TextView) findViewById(R.id.supplier_text_view);
     try{
     // Display the number of rows in the Cursor (In the database in this case)
     displayTextView.setText("Number of Rows in SUPPLIERS database table: " + cursor.getCount());
     //Extract the index of each column
     int idColumnIndex = cursor.getColumnIndex(SupplierEntry._ID);
     int nameColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_NAME);
     int telColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PHONE);
     int emailColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_EMAIL);
     while(cursor.moveToNext()){
     int currentID = cursor.getInt(idColumnIndex);
     String name = cursor.getString(nameColumnIndex);
     String phone = cursor.getString(telColumnIndex);
     String email = cursor.getString(emailColumnIndex);
     displayTextView.append("\n"+ currentID + " - " + name + " - " + phone + " - " + email );
     }
     }
     finally {
     // Always close the cursor when you're done reading from it. This releases all its resources and makes it invalid.
     cursor.close();
     }
     // Perform this raw SQL query "SELECT * FROM instruments" to get a Cursor that contains all rows from the pets table.
     cursor = db.rawQuery("SELECT * FROM " + InstrumentEntry.TABLE_NAME, null);

     displayTextView = (TextView) findViewById(R.id.instrument_text_view);
     try{
     // Display the number of rows in the Cursor (In the database in this case)
     displayTextView.setText("Number of Rows in INSTRUMENTS database table: " + cursor.getCount());
     //Extract the index of each column
     int idColumnIndex = cursor.getColumnIndex(InstrumentEntry._ID);
     int nameColumnIndex = cursor.getColumnIndex(InstrumentEntry.COLUMN_NAME);
     int serialColumnIndex = cursor.getColumnIndex(InstrumentEntry.COLUMN_SERIAL);
     int brandColumnIndex = cursor.getColumnIndex(InstrumentEntry.COLUMN_BRAND);
     int priceColumnIndex = cursor.getColumnIndex(InstrumentEntry.COLUMN_PRICE);
     int nbColumnIndex = cursor.getColumnIndex(InstrumentEntry.COLUMN_NB);
     int suppIdColumnIndex = cursor.getColumnIndex(InstrumentEntry.COLUMN_SUPPLIER_ID);

     while(cursor.moveToNext()){
     int currentID = cursor.getInt(idColumnIndex);
     String name = cursor.getString(nameColumnIndex);
     String serial = cursor.getString(serialColumnIndex);
     String brand = cursor.getString(brandColumnIndex);
     String price = cursor.getString(priceColumnIndex);
     String nb = cursor.getString(nbColumnIndex);
     String suppId = cursor.getString(suppIdColumnIndex);
     displayTextView.append("\n"+ currentID + " - " + name + " - " + serial + " - " + brand + " - " + price + " - " + nb + " - " + suppId);
     }
     }
     finally {
     // Always close the cursor when you're done reading from it. This releases all its resources and makes it invalid.
     cursor.close();
     }
     }*/


    /**
     * Helper method to insert hardcoded supplier data into the database. For debugging purposes only.
     */
    public void insertSupplier(View view){
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_NAME, "MUSICPLUS");
        values.put(SupplierEntry.COLUMN_EMAIL, "contact@musicplus.tn");
        values.put(SupplierEntry.COLUMN_PHONE, "71 343 025");
        Uri newUri = getContentResolver().insert(SupplierEntry.CONTENT_SUPPLIER_URI, values);
    }
    /**
     * Helper method to insert hardcoded supplier data into the database. For debugging purposes only.
     */
    public void insertInstrument(View v){
        ContentValues values = new ContentValues();
        values.put(InstrumentEntry.COLUMN_NAME, "Piano");
        values.put(InstrumentEntry.COLUMN_SERIAL, "GP148" );
        values.put(InstrumentEntry.COLUMN_BRAND, "PEARL RIVER PIANO");
        values.put(InstrumentEntry.COLUMN_PRICE, 9000.0 );
        values.put(InstrumentEntry.COLUMN_SUPPLIER_ID, 1);
        values.put(InstrumentEntry.COLUMN_NB, 5);
        Uri newUri = getContentResolver().insert(InstrumentEntry.CONTENT_INSTRUMENT_URI, values);
    }


}
