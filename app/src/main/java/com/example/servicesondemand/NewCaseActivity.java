package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewCaseActivity extends AppCompatActivity {

    private TextView categoryInfoTextView, detailsInfoTextView, phoneInfoTextView, addressTextView;
    private EditText categoryEditText, detailsEditText, phoneEditText, addressEditText;
    private Button submitButton, findMeButton;
    private String category, problemDetails, phoneNumber, address;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase, firebaseUserDatabase;
    DatabaseReference databaseReference, demoRef, databaseUserReference, demoUserRef;
    private FusedLocationProviderClient fusedLocationClient;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private void setupUIViews(){
        categoryInfoTextView = findViewById(R.id.categoryInfoTextView);
        detailsInfoTextView = findViewById(R.id.detailsInfoTextView);
        phoneInfoTextView = findViewById(R.id.phoneInfoTextView);
        categoryEditText = findViewById(R.id.categoryEditText);
        detailsEditText = findViewById(R.id.detailsEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        submitButton = findViewById(R.id.submitButton);
        findMeButton = findViewById(R.id.findMeButton);
        addressEditText = findViewById(R.id.addressEditText);
        addressTextView = findViewById(R.id.addressTextView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }
    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_case);
        setupUIViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUserDatabase = FirebaseDatabase.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };




        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    sendUserData();
                    Toast.makeText(getApplicationContext(), "Case added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainScreenActivity.class));
                }
            }
        });

        findMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), LocationActivity.class));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null){
                        updateLocationInfo(lastKnownLocation);
                    }
                }

            }
        });

    }

    private void updateLocationInfo(Location location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        address="";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0){

                if (listAddresses.get(0).getThoroughfare() != null){
                    address += listAddresses.get(0).getThoroughfare() + " ";
                }
                if (listAddresses.get(0).getLocality() != null){
                    address += listAddresses.get(0).getLocality() + " ";
                }
                if (listAddresses.get(0).getPostalCode() != null){
                    address += listAddresses.get(0).getPostalCode() + " ";
                }
                if (listAddresses.get(0).getAdminArea() != null){
                    address += listAddresses.get(0).getAdminArea() + " ";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addressEditText.setText(address);
    }

    private Boolean validate(){
        Boolean result = false;

        category = categoryEditText.getText().toString();
        problemDetails = detailsEditText.getText().toString();
        phoneNumber = phoneEditText.getText().toString();
        address = addressEditText.getText().toString();

        if(category.isEmpty() || problemDetails.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }
        return result;
    }

    private void sendUserData(){
        databaseReference = firebaseDatabase.getReference();
        demoRef = databaseReference.child("Cases");
        UserCase userCase = new UserCase(category, problemDetails, phoneNumber, address);
        demoRef.push().setValue(userCase);

        //Upload data to certain user Table
        databaseUserReference = firebaseUserDatabase.getReference(firebaseAuth.getUid());
        demoUserRef = databaseUserReference.child("Cases");
        demoUserRef.push().setValue(userCase);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Address getAddressForLocation(Context context, Location location) throws IOException {

        if (location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        int maxResults = 1;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
    }
}
