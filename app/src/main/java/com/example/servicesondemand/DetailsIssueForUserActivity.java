package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsIssueForUserActivity extends AppCompatActivity {

    private TextView titleTextView, categoryTextView, descriptionInfotextView, descriptionTextView, phoneInfoTextView, phoneTextView, addressInfoTextView;
    private Button goBackButton, deleteButton, mapButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase, firebaseUserDatabase;
    private DatabaseReference databaseReference, databaseUserReference, demoRef, demoUserRef;

    private String category, description, phoneNumber, address;

    private void setupUIViews(){
        titleTextView = findViewById(R.id.titleTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        descriptionInfotextView = findViewById(R.id.descriptionInfotextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        phoneInfoTextView = findViewById(R.id.phoneInfoTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        addressInfoTextView = findViewById(R.id.addressInfotextView);
        goBackButton = findViewById(R.id.goBackButton);
        deleteButton = findViewById(R.id.deleteButton);
        mapButton = findViewById(R.id.mapButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_issue_for_user);
        setupUIViews();

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        description = intent.getStringExtra("details");
        phoneNumber = intent.getStringExtra("phoneNumber");
        address = intent.getStringExtra("address");

        categoryTextView.setText(category);
        descriptionTextView.setText(description);
        phoneTextView.setText(phoneNumber);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUserDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();
        databaseUserReference = firebaseUserDatabase.getReference(firebaseAuth.getUid());

        demoRef = databaseReference.child("Cases");
        demoUserRef = databaseUserReference.child("Cases");

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), UserIssuesActivity.class));
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailsIssueForUserActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Are you that you want permanently delete this case?")
                        .setPositiveButton("No",null)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                demoRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            UserCase userCase = snapshot.getValue(UserCase.class);
                                            if (userCase.getCategory().equals(category) && userCase.getDetails().equals(description) && userCase.getPhoneNumber().equals(phoneNumber)){
                                                snapshot.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                demoUserRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            UserCase userCase = snapshot.getValue(UserCase.class);
                                            if (userCase.getCategory().equals(category) && userCase.getDetails().equals(description) && userCase.getPhoneNumber().equals(phoneNumber)){
                                                snapshot.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                finish();
                                Toast.makeText(getApplicationContext(), "Case Deleted Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserIssuesActivity.class));
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
