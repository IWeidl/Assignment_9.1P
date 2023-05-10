package com.iweidl.assignment71p;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iweidl.assignment71p.databinding.ActivityShowOnMapBinding;
import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;
import com.iweidl.assignment71p.sqlitehelper.LostFoundItem;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private ActivityShowOnMapBinding binding;

    private LostFoundDBHelper dbHelper;
    private List<LostFoundItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShowOnMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHelper = new LostFoundDBHelper(this);
        items = dbHelper.getAllItems();

        // Needed to allow main thread to execute network requests.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng melbourne = new LatLng(-37.81, 144.96);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 10));

        AddAllItems(googleMap);
    }

    private void AddAllItems(GoogleMap googleMap) {
        if (items != null) {
            for (LostFoundItem item : items) {
                String locationAddress = item.getLocation();
                LatLng latLng = getLatLngFromAddress(locationAddress);

                if (latLng != null) {
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(item.getStatus() + ": " + item.getName()));
                }
            }
        }
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        LatLng latLng = null;

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }
}