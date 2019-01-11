package com.example.android.musicinventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.musicinventory.data.InventContract;
import com.example.android.musicinventory.data.InventContract.*;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int inventLoader = 0;
    final Context context=this;
    Dialog myDialog;
    private Uri currentInstUri;
    private TextView mInstrument;
    private TextView mSerial;
    private TextView mBrand;
    private TextView mQuantity;
    private TextView mPrice;
    private TextView mSupplier;
    private String mPhone = "";
    private String mEmail = "";
    private boolean mQuantityChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Retrieve the Uri sent with intent to open this Activity
        currentInstUri = getIntent().getData();

        //Find all views activity_details layout
        mInstrument = (TextView) findViewById(R.id.nameDetails);
        mSerial = (TextView) findViewById(R.id.serialDetails);
        mBrand = (TextView) findViewById(R.id.brandDetails);
        mQuantity = (TextView) findViewById(R.id.quantityDetails);
        mPrice = (TextView) findViewById(R.id.priceDetails);
        mSupplier = (TextView) findViewById(R.id.supplierDetails);
        //Initialize loader to query database
        getLoaderManager().initLoader(inventLoader, null, this);



        myDialog = new Dialog(this);
        //Find Order button
        Button orderButton = (Button) findViewById(R.id.orderBtn);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask user to choose how to contact supplier by phone or by email
                showOrderDialog();
            }
        });
        //Update Quantity
        Button btnPlus = (Button) findViewById(R.id.plus_btn);
        Button btnMinus = (Button) findViewById(R.id.minus_btn);
        //Set the Plus and Minus Buttons to increase and decrease Quantity
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve a string from Quantity TextView and convert it to int
                int quantity = Integer.parseInt(mQuantity.getText().toString());
                //Increment Quantity
                quantity++;
                //Display new quantity
                mQuantity.setText(Integer.toString(quantity));
                mQuantityChanged = true;
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve a string from Quantity TextView and convert it to int
                int quantity = Integer.parseInt(mQuantity.getText().toString());
                if(quantity == 0){
                    Toast.makeText(context, "Quantity can't be Negative!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Decrement Quantity
                quantity--;
                //Display new quantity
                mQuantity.setText(Integer.toString(quantity));
                mQuantityChanged = true;
            }
        });
    }

    /**
     *Show the Order dialog for the user to make him choose between ordering by Email or by Phone
     */
    public void showOrderDialog(){
        //Set order_dialog.xml as a layout to the Dialog window
        myDialog.setContentView(R.layout.order_dialog);
        Button btnPhone = (Button) myDialog.findViewById(R.id.phone_btn_view);
        Button btnEmail = (Button) myDialog.findViewById(R.id.email_btn_view);
        //Set the Phone button to send an intent to the Phone Dialer app
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                //Use Uri scheme to specify the Phone number of the called person
                phoneIntent.setData(Uri.parse("tel:" + mPhone));
                if(phoneIntent.resolveActivity(getPackageManager())!=null) {
                    startActivity(phoneIntent);
                }
                //Close Order Dialog after creating Phone Intent
                myDialog.dismiss();
            }
        });
        //Set the Email button to send an intent to an Email app
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = "Instrument Order";
                String [] addresses = {mEmail};
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // Uri scheme. Only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
                //Close Order Dialog after creating Email Intent
                myDialog.dismiss();
            }
        });



        //Make the parent window of the dialog transparent for visual polish
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Show the dialog
        myDialog.show();
    }

    /**
     * Close Order Dialog
     * The ClickListener event is attached via the attribute android:onClick. I could not perform it with Java code
     */
    public void closeOrderDialog(View view){
        myDialog.dismiss();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                InstrumentEntry._ID,
                InstrumentEntry.COLUMN_NAME,
                InstrumentEntry.COLUMN_SERIAL,
                InstrumentEntry.COLUMN_BRAND,
                InstrumentEntry.COLUMN_NB,
                InstrumentEntry.COLUMN_PRICE,
                InstrumentEntry.COLUMN_SUPPLIER_ID};

        return new CursorLoader(this,
                currentInstUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Proceed with moving to the first row of the cursor and reading data from it
        if(data.moveToFirst()){
            // Find the columns of instrument attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(InstrumentEntry.COLUMN_NAME);
            int serialColumnIndex = data.getColumnIndex(InstrumentEntry.COLUMN_SERIAL);
            int brandColumnIndex = data.getColumnIndex(InstrumentEntry.COLUMN_BRAND);
            int quantityColumnIndex = data.getColumnIndex(InstrumentEntry.COLUMN_NB);
            int priceColumnIndex = data.getColumnIndex(InstrumentEntry.COLUMN_PRICE);
            int suppIdColumnIndex = data.getColumnIndex(InstrumentEntry.COLUMN_SUPPLIER_ID);
            //Extract values from cursor
            String instrument = data.getString(nameColumnIndex);
            String serial = data.getString(serialColumnIndex);
            String brand = data.getString(brandColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            float price = data.getFloat(priceColumnIndex);
            int suppId = data.getInt(suppIdColumnIndex);
            //Query Suppliers table to get Name, Phone and Emails
            String [] projection = {
                    SupplierEntry.COLUMN_NAME,
                    SupplierEntry.COLUMN_PHONE,
                    SupplierEntry.COLUMN_EMAIL};
            String selection = SupplierEntry._ID + "=?";
            String [] selectionArgs = {Integer.toString(suppId)};
            Cursor cursor = getContentResolver().query(SupplierEntry.CONTENT_SUPPLIER_URI,
                    projection, selection, selectionArgs, null);
            try {
                if(cursor.moveToFirst()){
                    int suppColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_NAME);
                    int phoneColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PHONE);
                    int emailColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_EMAIL);

                    String supplier = cursor.getString(suppColumnIndex);
                    mPhone = cursor.getString(phoneColumnIndex);
                    mEmail = cursor.getString(emailColumnIndex);
                    mSupplier.setText(supplier);
                }

            }finally {
                cursor.close();
            }

            //Set TextViews
            mInstrument.setText(instrument);
            mSerial.setText(serial);
            mBrand.setText(brand);
            mQuantity.setText(Integer.toString(quantity));
            mPrice.setText(Float.toString(price)+ " TND");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mInstrument.setText("");
        mSerial.setText("");
        mBrand.setText("");
        mQuantity.setText("");
        mPrice.setText("");
    }
    /**
     * Inflate the menu options from the res/menu/menu_editor.xml file.
     * This adds menu items to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.save:
                saveQuantity();
                finish();//Exit Activity
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If quantity hasn't changed, continue with navigating up to parent activity (MainActivity)
                if(!mQuantityChanged){
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    }
                };
                showUnsavedChangeDialog(discardOnClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If Quantity hasn't changed, continue with handling back button press
        if(!mQuantityChanged){
            super.onBackPressed();
            return;
        }
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        showUnsavedChangeDialog(discardOnClickListener);

    }

    /**
     * Create an Alert dialog to warn user of unsaved changes
     */
    private void showUnsavedChangeDialog(DialogInterface.OnClickListener discardOnClickListener) {
        // Create the builder of alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the message for the dialog
        builder.setMessage("Discard quantity changes ?");
        // Set click listener for the positive button on the dialog
        builder.setPositiveButton("Discard", discardOnClickListener);
        // Set click listener for the negative button on the dialog
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Keep editing" button, so dismiss the dialog
                        if(dialog != null)
                            dialog.dismiss();
                    }
                }
        );
        //Create alert dialog
        AlertDialog alert = builder.create();
        //Show alert dialog
        alert.show();
    }

    /**
     * Save the changed quantity in the database
     */
    private void saveQuantity() {
        //Retrieve Quantity TextView from layout
        int quantity = Integer.parseInt(mQuantity.getText().toString());
        ContentValues values = new ContentValues();
        values.put(InstrumentEntry.COLUMN_NB, quantity);
        int row = getContentResolver().update(currentInstUri, values, null, null);
        if(row != 0){
            Toast.makeText(DetailsActivity.this, "Quantity successfully updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(DetailsActivity.this, "Updating quantity failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows an Alert dialog  for the user to confirm deleting the intrument
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sur you want to delete this Instrument?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteInstrument();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
    }

    /**
     * Delete an instrument from database
     */
    private void deleteInstrument() {
        if(currentInstUri != null){
            int rows = getContentResolver().delete(currentInstUri, null, null);
            if(rows != 0){
                Toast.makeText(this, "Instrument deleted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Error with deleting Instrument", Toast.LENGTH_SHORT).show();
            }
            //Close Activity
            finish();

        }

    }

}
