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


        dbHelper = new LostFoundDBHelper(this);

        Intent intent = getIntent();
        itemId = intent.getIntExtra("ITEM_ID", -1);

        item = dbHelper.getItem(itemId);

        if (item != null) {
            textViewTitle.setText(item.getStatus() + ": " + item.getName());
            ShowDateDifference();
            textViewItemLocation.setText("Location: " + item.getLocation());
            textViewItemPhone.setText("Please Call: " + item.getPhone());
        }

        buttonRemove.setOnClickListener(view -> {
            dbHelper.deleteItem(itemId);
            finish();
        });
    }

    private void ShowDateDifference() {
        Date itemFoundDate = item.getDate();
        Date now = Calendar.getInstance().getTime();
        long differenceInMilliseconds = Math.abs(now.getTime() - itemFoundDate.getTime());
        long differenceInDays = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
        String dateDifferenceText;

        if (differenceInDays == 0)
            dateDifferenceText = item.getStatus() + ": " + "Today";
        else
            dateDifferenceText = item.getStatus() + ": " + differenceInDays + " days ago";

        textViewItemDate.setText(dateDifferenceText);
    }
}