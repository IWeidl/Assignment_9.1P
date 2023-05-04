package com.iweidl.assignment71p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonCreateNew;
    Button buttonShowAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCreateNew = findViewById(R.id.buttonCreateNew);
        buttonShowAll = findViewById(R.id.buttonShowAll);


        // Set onClick listener for both buttons to start their respective Activity
        buttonCreateNew.setOnClickListener(view -> {
            StartCreateNewActivity();
        });

        buttonShowAll.setOnClickListener(view -> {
            StartShowAllActivity();
        });
    }

    protected void StartCreateNewActivity()
    {
        Intent intent = new Intent(this, CreateNewActivity.class);
        startActivity(intent);
    }

    protected void StartShowAllActivity()
    {
        Intent intent = new Intent(this, ShowAllLostAndFoundActivity.class);
        startActivity(intent);
    }
}