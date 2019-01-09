package com.example.android.musicinventory;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.android.musicinventory.data.InventContract.*;

public class EditorActivity extends AppCompatActivity {
    final Context context=this;
    Dialog myDialog;
    private static final int REQUEST_IMAGE_CAPTURE = 10;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;
    private TextView imageName;
    private String mPhotoPath;
    private EditText mInstrument;
    private EditText mSerial;
    private EditText mBrand;
    private EditText mPrice;
    private EditText mSupplier;
    private EditText mEmail;
    private EditText mTel;
    private NumberPicker mQuantity;
    private Spinner mSpinner;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //Configure the Quantity Number Picker
        mQuantity = (NumberPicker) findViewById(R.id.qtyNbPicker);
        //Set the minimum value of the Quantity Number Picker
        mQuantity.setMinValue(0);
        //Set the minimum value of the Quantity Number Picker
        mQuantity.setMaxValue(100);
        //Gets whether the selector wheel wraps when reaching the min/max value.
        mQuantity.setWrapSelectorWheel(true);
        //Create new supplier Dialog
        myDialog = new Dialog(this);
        //Find EditText in xml layout
        mInstrument = (EditText) findViewById(R.id.edit_instrument);
        mBrand = (EditText) findViewById(R.id.edit_brand);
        mSerial = (EditText) findViewById(R.id.edit_serial);
        mPrice = (EditText) findViewById(R.id.edit_price);
        mSupplier = (EditText) findViewById(R.id.edit_supplier);
        mEmail = (EditText) findViewById(R.id.edit_email);
        mTel = (EditText) findViewById(R.id.edit_tel);
        mSpinner = (Spinner) findViewById(R.id.supplier_spinner);

        //Load data from suppliers table to power Spinner
        loadSupplierSpinner();


        //Gallery Intent- Find the Choose Image button
        ImageButton chooseImage = (ImageButton) findViewById(R.id.chooseImage);
        //Set an OnClickListener to the Choose Image Button to open Gallery
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invoke the Image Gallery using an implicit intent. Intent.Action_Pick is a String to tell Android I want to pick sth
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                //Where do we want to find the data
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //Get the name of the directory
                String pictureDirectoryPath = pictureDirectory.getPath();
                //Get a URI representation of the Path because this is what android needs to deal with
                Uri data = Uri.parse(pictureDirectoryPath);
                //Set the data (where we want to look for this media) and the type (what media do we want to look for).Get all image types
                photoPickerIntent.setDataAndType(data, "image/*");
                //We invoke the image gallery and receive something from it
                if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
                }

            }
        });
        //Find the ImageView
        imgPicture = (ImageView) findViewById(R.id.instrument_image);
        //Find the image TextView
        imageName = (TextView) findViewById(R.id.imageName);

        //Camera Intent-Find the take Photo button
        ImageButton takePhoto = (ImageButton) findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeCamera();
            }
        });

        //New Supplier Button Click
        Button newSupplier = (Button) findViewById(R.id.new_supp_btn);
        newSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewSupplierDialog();
            }
        });

    }

    /**
     * Shows a dialog to input new supplier info and save them in database (table suppliers)
     */
    private void showNewSupplierDialog() {
        //Set order_dialog.xml as a layout to the Dialog window
        myDialog.setContentView(R.layout.new_supplier_dialog);
        //Program the buttons
        Button btnSave = (Button) myDialog.findViewById(R.id.save_btn);
        Button btnCancel = (Button) myDialog.findViewById(R.id.cancel_btn);
        TextView btnClose = (TextView) myDialog.findViewById(R.id.close_txt_view);
        final EditText editName = (EditText) myDialog.findViewById(R.id.edit_name);
        final EditText editTel = (EditText) myDialog.findViewById(R.id.edit_tel);
        final EditText editEmail = (EditText) myDialog.findViewById(R.id.edit_email);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Strings from EditTexts
                String name = editName.getText().toString().trim();
                String tel  = editTel.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                //Put information in ContentValues
                ContentValues values = new ContentValues();
                values.put(SupplierEntry.COLUMN_NAME, name);
                values.put(SupplierEntry.COLUMN_PHONE, tel);
                values.put(SupplierEntry.COLUMN_EMAIL, email);
                //call insert method
                Uri uri = getContentResolver().insert(SupplierEntry.CONTENT_SUPPLIER_URI, values);
                if(uri != null){
                    loadSupplierSpinner();
                    mSpinner.setSelection(spinnerAdapter.getPosition(name));
                    Toast.makeText(context,"Supplier Saved Successfully", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(context, "Error with saving supplier", Toast.LENGTH_SHORT).show();
                }
                myDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        //Make the parent window of the dialog transparent for visual polish
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Show the dialog
        myDialog.show();
    }

    private ArrayList<String> getAllSuppliers(){
        ArrayList<String> suppliers = new ArrayList<String>();
        String [] projection = {SupplierEntry._ID, SupplierEntry.COLUMN_NAME};
        //Select Query
        Cursor cursor = getContentResolver().query(SupplierEntry.CONTENT_SUPPLIER_URI, projection, null, null, null);
        try {
            int nameColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_NAME);
            //move the cursor to the 0th position, before you start extracting out column values from it
            if (cursor.moveToFirst()) {
                do {
                    suppliers.add(cursor.getString(nameColumnIndex));
                } while (cursor.moveToNext());
            }
            return suppliers;
        }finally {
            cursor.close();
        }
    }

    /**
     *Power the Spinner from database table Suppliers
     */
    private void loadSupplierSpinner() {
        //Retrieve Suppliers list from database
        ArrayList<String> suppliers = getAllSuppliers();
        //Create Adapter for Spinner
        spinnerAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, suppliers);
        // Specify dropdown layout style - simple list view with 1 item per line
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // attaching data adapter to spinner
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String supplier = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "You selected: " + supplier, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing for now
            }
        });
    }



    /**
     * Create a file to save the taken photo
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //The directory where to save the file image
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //The actual image file
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void invokeCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, R.string.noFile, Toast.LENGTH_LONG).show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //The camera intent needs information (to know where to save photo) as a URI and not File
                //So convert File to URI
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.musicinventory.myFileProvider",
                        photoFile);
                //Set information to the intent
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //If we are here, everything processed normally
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                //If we are here, we are hearing back from the Gallery

                //The address of the image on the SD Card.
                Uri imageUri = data.getData();
                //declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                try {
                    //We are getting an input stream based of the URI of the image
                    inputStream = getContentResolver().openInputStream(imageUri);
                    //Get a bitmap from the stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    //Show the image to the user
                    imgPicture.setImageBitmap(image);
                    imageName.setText(R.string.yes_image);
                    imageName.setBackgroundColor(getResources().getColor(R.color.yes_image));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //Show a message to the user indicating that the image is not available
                    Toast.makeText(this, "Unable to Open image", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Toast.makeText(this, R.string.pictureSaved, Toast.LENGTH_LONG).show();
                imageName.setText(R.string.yes_image);
                imageName.setBackgroundColor(getResources().getColor(R.color.yes_image));
            }
        }

    }
    /**
     * Inflate the menu options from the res/menu/menu_editor.xml file.
     * This adds menu items to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        MenuItem deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                // Respond to a click on the "Save" menu option
                saveInstrument();
                finish(); //Exit Activity
                return true;
            case R.id.home:
                //Don't forget to implement the home button click
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save instrument details input by user in database
     */
    private void saveInstrument() {
        String instrument = mInstrument.getText().toString().trim();
        String brand = mBrand.getText().toString().trim();
        String serial = mSerial.getText().toString().trim();
        int quantity = mQuantity.getValue();
        String priceString = mPrice.getText().toString().trim();
        if(TextUtils.isEmpty(instrument) && TextUtils.isEmpty(brand) && TextUtils.isEmpty(serial) &&
                TextUtils.isEmpty(priceString) && quantity == 0){
            return;
        }
        float price = 0;
        if(!TextUtils.isEmpty(priceString)){
            price = Float.parseFloat(priceString);
        }
        //Retrieve selected supplier from Spinner
        String suppName = mSpinner.getSelectedItem().toString();
        //A query request to get the id of the selected supplier
        String [] projection = {SupplierEntry._ID, SupplierEntry.COLUMN_NAME};
        String selection = SupplierEntry.COLUMN_NAME + "=?";
        String [] selectionArgs = {suppName};
        Cursor cursor = getContentResolver().query(SupplierEntry.CONTENT_SUPPLIER_URI, projection, selection, selectionArgs, null);
        int idSupplier = 0;
        try {
            int idColumnIndex = cursor.getColumnIndex(SupplierEntry._ID);
            //move the cursor to the 0th position, before you start extracting out column values from it
            if (cursor.moveToFirst()) {
                idSupplier = cursor.getInt(idColumnIndex);
            }
        }finally {
            cursor.close();
        }
        ContentValues values = new ContentValues();
        values.put(InstrumentEntry.COLUMN_NAME, instrument);
        values.put(InstrumentEntry.COLUMN_BRAND, brand);
        values.put(InstrumentEntry.COLUMN_SERIAL, serial);
        values.put(InstrumentEntry.COLUMN_PRICE, price);
        values.put(InstrumentEntry.COLUMN_NB, quantity);
        values.put(InstrumentEntry.COLUMN_SUPPLIER_ID, idSupplier);
        Uri uri = getContentResolver().insert(InstrumentEntry.CONTENT_INSTRUMENT_URI, values);
        if(uri != null){
            Toast.makeText(this, "Instrument successfully inserted", Toast.LENGTH_SHORT);
        }else
            Toast.makeText(this, "Instrument insertion failed", Toast.LENGTH_SHORT);
    }
}

