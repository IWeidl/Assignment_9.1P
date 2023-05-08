package com.iweidl.assignment71p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateNewActivity extends AppCompatActivity {
    private RadioGroup radioGroupLostFound;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextDescription;
    private EditText editTextDate;
    private EditText editTextLocation;
    private Button buttonSave;
    private Button buttonGetCurrentLocation;
    Location currentLocation;

    private LostFoundDBHelper dbHelper;

    SimpleDateFormat dateFormat;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 0, locationListener);
            }
        }
    }

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

        buttonGetCurrentLocation = findViewById(R.id.buttonGetCurrentLocation);

        // Define a format to be used for parsing editTextDate
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Set editTextDates to today's date by default.
        editTextDate.setText(dateFormat.format(new Date()));

        // Initialize the dbHelper for use later
        dbHelper = new LostFoundDBHelper(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 0, locationListener);

        // saveItem is called when buttonSave is tapped
        buttonSave.setOnClickListener(view -> {
            saveItem();
        });

        buttonGetCurrentLocation.setOnClickListener(view -> {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            editTextLocation.setText(currentLocation.toString());

            if (currentLocation != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        editTextLocation.setText(address.getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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