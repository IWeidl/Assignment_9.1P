package com.iweidl.assignment71p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;
import com.iweidl.assignment71p.sqlitehelper.LostFoundItem;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ItemDetailsActivity extends AppCompatActivity {
    TextView textViewTitle;
    Button buttonRemove;
    TextView textViewItemDate;
    TextView textViewItemLocation;
    TextView textViewItemPhone;

    private LostFoundDBHelper dbHelper;
    private int itemId;
    LostFoundItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        textViewTitle = findViewById(R.id.textViewTitle);
        buttonRemove = findViewById(R.id.buttonRemove);
        textViewItemDate = findViewById(R.id.textViewItemDate);
        textViewItemLocation = findViewById(R.id.textViewItemLocation);
        textViewItemPhone = findViewById(R.id.textViewItemPhone);

        // Initialie the dbHelper for later use
        dbHelper = new LostFoundDBHelper(this);

        // Get the itemId that was passed in the intent
        Intent intent = getIntent();
        itemId = intent.getIntExtra("ITEM_ID", -1);

        // Retrieve this item from the database using the ID passed above
        item = dbHelper.getItem(itemId);

        // Provided the item actually exists, update this activities TextViews with the items details
        if (item != null) {
            textViewTitle.setText(item.getStatus() + ": " + item.getName());

            // Broken out into own method due to size
            ShowDateDifference();

            textViewItemLocation.setText("Location: " + item.getLocation());
            textViewItemPhone.setText("Please Call: " + item.getPhone());
        }

        // Delete the item from the database and then close this activity
        buttonRemove.setOnClickListener(view -> {
            dbHelper.deleteItem(itemId);
            finish();
        });
    }

    private void ShowDateDifference() {
        // Get the date the item was found
        Date itemFoundDate = item.getDate();
        // Get the current date
        Date now = Calendar.getInstance().getTime();
        // Work out the time difference between these two dates in millisecondds
        long differenceInMilliseconds = Math.abs(now.getTime() - itemFoundDate.getTime());
        // Convert this time difference to DAYS
        long differenceInDays = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);

        // Show text depending on whether the item was found today or earlier
        String dateDifferenceText;
        if (differenceInDays == 0)
            dateDifferenceText = item.getStatus() + ": " + "Today";
        else
            dateDifferenceText = item.getStatus() + ": " + differenceInDays + " days ago";

        textViewItemDate.setText(dateDifferenceText);
    }
}