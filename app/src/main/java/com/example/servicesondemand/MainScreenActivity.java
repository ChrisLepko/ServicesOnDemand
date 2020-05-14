package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainScreenActivity extends AppCompatActivity {
    private TextView info1TextView, info2TextView, nameInfoTextView;
    private Button newCaseButton, userIssuesButton, allIssuesButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private void setupUIViews(){
        info1TextView = findViewById(R.id.info1TextView);
        info2TextView = findViewById(R.id.info2TextView);
        nameInfoTextView = findViewById(R.id.nameInfoTextView);
        newCaseButton = findViewById(R.id.newCaseButton);
        userIssuesButton = findViewById(R.id.userIssuesButton);
        allIssuesButton = findViewById(R.id.allIssuesButton);
    }

    private void logout(){
        try {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setupUIViews();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Checks if user is already logged in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainScreenActivity.class));
        }


        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        DatabaseReference userDatabaseReference = databaseReference.child("UserProfile");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                nameInfoTextView.setText(userProfile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        newCaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewCaseActivity.class));
            }
        });

        userIssuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserIssuesActivity.class));
            }
        });

        allIssuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AllIssuesActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
                case R.id.logout:
                    logout();
                    break;
                case R.id.profile:
                    startActivity(new Intent(MainScreenActivity.this, ProfileActivity.class));
                    break;
                default:
                    break;

        }

        return super.onOptionsItemSelected(item);
    }
}
