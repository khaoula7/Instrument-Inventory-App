package com.example.android.musicinventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity {
    final Context context=this;
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        myDialog = new Dialog(this);
        //Find Order button
        Button orderButton = (Button) findViewById(R.id.orderBtn);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask user to choose haw to contact supplier by phone or by email
                showOrderDialog(v);
            }
        });

        Button btnPlus = (Button) findViewById(R.id.plus_btn);
        Button btnMinus = (Button) findViewById(R.id.minus_btn);
        final TextView txtQty = (TextView) findViewById(R.id.qty_txt_view);
        //Set the Plus and Minus Buttons to increase and decrease Quantity
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve a string from Quantity TextView and convert it to int
                int quantity = Integer.parseInt(txtQty.getText().toString());
                //Increment Quantity
                quantity++;
                //Display new quantity
                txtQty.setText(Integer.toString(quantity));
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve a string from Quantity TextView and convert it to int
                int quantity = Integer.parseInt(txtQty.getText().toString());
                if(quantity == 0){
                    Toast.makeText(context, "Quantity can't be Negative!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Decrement Quantity
                quantity--;
                //Display new quantity
                txtQty.setText(Integer.toString(quantity));
            }
        });

    }

    /**
     *Show the Order dialog for the user to make him choose between ordering by Email or by Phone
     */
    public void showOrderDialog(View view){
        //Set order_dialog.xml as a layout to the Dialog window
        myDialog.setContentView(R.layout.order_dialog);
        Button btnPhone = (Button) myDialog.findViewById(R.id.phone_btn_view);
        Button btnEmail = (Button) myDialog.findViewById(R.id.email_btn_view);
        //Set the Phone button to send an intent to the Phone Dialer app
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                if(phoneIntent.resolveActivity(getPackageManager())!=null) {
                    startActivity(phoneIntent);
                }
            }
        });
        //Set the Email button to send an intent to an Email app
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = "Instrument Order";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
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

    /**
     * Inflate the menu options from the res/menu/menu_editor.xml file.
     * This adds menu items to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

}
