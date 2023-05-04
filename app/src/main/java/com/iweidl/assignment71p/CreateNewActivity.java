package com.iweidl.assignment71p;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNewActivity extends AppCompatActivity {
    private RadioGroup radioGroupLostFound;
    private RadioButton radioLost;
    private RadioButton radioFound;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextDescription;
    private EditText editTextDate;
    private EditText editTextLocation;
    private Button buttonSave;

    private LostFoundDBHelper dbHelper;

    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

        radioGroupLostFound = findViewById(R.id.radioGroupLostFound);
        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.buttonSave);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextDate.setText(dateFormat.format(new Date()));

        dbHelper = new LostFoundDBHelper(this);

        buttonSave.setOnClickListener(view -> {
            saveItem();
        });
    }

    private void saveItem() {
        int radioButtonId = radioGroupLostFound.getCheckedRadioButtonId();
        String status = radioButtonId == R.id.radioLost ? "Lost" : "Found";

        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dateString = editTextDate.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format, use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.insertItem(status, name, phone, description, date, location)) {
            Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
        }
    }

}