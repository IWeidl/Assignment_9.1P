package com.iweidl.assignment71p;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    // If Location permissions are granted, check for the current location once ever 200ms, so we can use GetLastKnownLocation() later.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, locationListener);
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

        // Get the location system service
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // Though locationListener is needed for locationManager to run correctly, it's not direclty used,
        // so I have just implemented a blank override
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };

        // Check if the app has permissions and if not, request them.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, locationListener);

        // saveItem is called when buttonSave is tapped
        buttonSave.setOnClickListener(view -> {
            saveItem();
        });

        buttonGetCurrentLocation.setOnClickListener(view -> {
            // Get the last known location from the locationManager()
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (currentLocation != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    // Use the GeoCoder API to get the address line from the LatLn, setting editTextLocation's text to this value
                    // This is because I am storing the address line in the database, not the LatLn
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

        // Initialize Places if it isn't already
        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDH3J1iKZFcfxY1jb6Xq5rNdOITHsPFEow");
        }

        // When the editTextLocation field is clicked, open the auto complete activity, passing in
        // the intent, and using startAutoComplete (defined below)
        editTextLocation.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startAutocomplete.launch(intent);
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

    // Instance of ActivityResultLauncher, used because google docs said that startActivityForResult()
    // is deprecated, and this should be used instead.
    // Apart from that, it's general logic, if the result is OK, set editTextLocation's text to the returned address
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    Place place = Autocomplete.getPlaceFromIntent(intent);
                    editTextLocation.setText(place.getAddress());
                }
            }
        });

}