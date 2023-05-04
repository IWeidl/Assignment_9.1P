package com.iweidl.assignment71p;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNewActivity extends AppCompatActivity {
    private RadioGroup radioGroupLostFound;
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
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.buttonSave);

        // Define a format to be used for parsing editTextDate
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Set editTextDates to today's date by default.
        editTextDate.setText(dateFormat.format(new Date()));

        // Initialize the dbHelper for use later
        dbHelper = new LostFoundDBHelper(this);

        // saveItem is called when buttonSave is tapped
        buttonSave.setOnClickListener(view -> {
            saveItem();
        });
    }

    private void saveItem() {
        // Work out which of the two radio buttons are ticked, setting status appropriately
        int radioButtonId = radioGroupLostFound.getCheckedRadioButtonId();
        String status = radioButtonId == R.id.radioLost ? "Lost" : "Found";

        // Retrieve all the entered values from the form, removing unnecessary whitespace
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dateString = editTextDate.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        // Extra logic to ensure the date is in the correct format, showing a toast if not
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format, use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt to save to the DB, showing the appropriate toast depending on the result of the database result
        if (dbHelper.insertItem(status, name, phone, description, date, location)) {
            Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
        }
    }

}