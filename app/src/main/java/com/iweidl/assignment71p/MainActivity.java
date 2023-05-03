package com.iweidl.assignment71p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonCreateNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCreateNew = findViewById(R.id.buttonCreateNew);



        buttonCreateNew.setOnClickListener(view -> {
            StartCreateNewActivity();
        });
    }

    protected void StartCreateNewActivity()
    {
        Intent intent = new Intent(this, CreateNewActivity.class);
        startActivity(intent);
    }
}