package com.example.android.musicinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.musicinventory.data.InventContract;

import org.w3c.dom.Text;

public class InventCursorAdapter extends CursorAdapter {

    public InventCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
    }

    /**
     * This method binds the instrument data (in the current row pointed to by cursor) to the given list item layout
     * @param view Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView thumbnailImage = (ImageView) view.findViewById(R.id.thumbnail);
        TextView instrumentTextView = (TextView) view.findViewById(R.id.instrument_txt);
        TextView serialTextView = (TextView) view.findViewById(R.id.serial_txt);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_txt);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_txt);
        Button saleButton = (Button) view.findViewById(R.id.sale_btn);

        //Find the columns of instrument attributes that we are interested in
        int instrumentColumnIndex = cursor.getColumnIndex(InventContract.InstrumentEntry.COLUMN_NAME);
        int serialColumnIndex = cursor.getColumnIndex(InventContract.InstrumentEntry.COLUMN_SERIAL);
        int quantityColumnIndex = cursor.getColumnIndex(InventContract.InstrumentEntry.COLUMN_NB);
        int priceColumnIndex = cursor.getColumnIndex(InventContract.InstrumentEntry.COLUMN_PRICE);

        //Read the instrument attributes from from the cursor for the current instrument
        String instrument = cursor.getString(instrumentColumnIndex);
        String serial = cursor.getString(serialColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        String quantityString = Integer.toString(quantity);
        String priceString = Float.toString(price);

        // Update the textViews with the attributes from cursor
        instrumentTextView.setText(instrument);
        serialTextView.setText(serial);
        quantityTextView.setText(quantityString);
        priceTextView.setText(priceString);

    }
}
