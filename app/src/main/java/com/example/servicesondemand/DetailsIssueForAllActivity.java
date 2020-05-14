package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsIssueForAllActivity extends AppCompatActivity {

    private TextView titleTextView, categoryTextView, descriptionInfotextView, descriptionTextView, phoneInfoTextView, phoneTextView, addressInfoTextView;
    private Button confirmButton, goBackButton, mapButton;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, demoRef;

    private String category, description, phoneNumber, address;

    private void setupUIViews(){
        titleTextView = findViewById(R.id.titleTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        descriptionInfotextView = findViewById(R.id.descriptionInfotextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        phoneInfoTextView = findViewById(R.id.phoneInfoTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        addressInfoTextView = findViewById(R.id.addressInfotextView);
        confirmButton = findViewById(R.id.confirmButton);
        goBackButton = findViewById(R.id.goBackButton);
        mapButton = findViewById(R.id.mapButton);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_issue_for_all);
        setupUIViews();

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        description = intent.getStringExtra("details");
        phoneNumber = intent.getStringExtra("phoneNumber");
        address = intent.getStringExtra("address");

        categoryTextView.setText(category);
        descriptionTextView.setText(description);
        phoneTextView.setText(phoneNumber);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), AllIssuesActivity.class));
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(DetailsIssueForAllActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Are you sure that you can help with this issue? Before that make sure that you have customer's phone number: " + phoneNumber)
                        .setPositiveButton("No", null)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseDatabase = FirebaseDatabase.getInstance();
                                databaseReference = firebaseDatabase.getReference();
                                demoRef = databaseReference.child("Cases");

                                demoRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            UserCase userCase = snapshot.getValue(UserCase.class);
                                            if (userCase.getCategory().equals(category) && userCase.getDetails().equals(description) && userCase.getPhoneNumber().equals(phoneNumber)){
                                                snapshot.getRef().removeValue();
                                            }
                                        }
                                        Toast.makeText(getApplicationContext(), "Job Confirmed Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), AllIssuesActivity.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .show();

            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });

    }
}
