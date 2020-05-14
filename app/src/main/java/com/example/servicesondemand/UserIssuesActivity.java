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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserIssuesActivity extends AppCompatActivity {


    static ArrayList<String> categories = new ArrayList<>();
    static ArrayList<String> details = new ArrayList<>();
    static ArrayList<String> phoneNumbers = new ArrayList<>();
    static ArrayList<String> address = new ArrayList<>();
//    static ArrayList<String> allCategories = new ArrayList<>();
//    static ArrayList<String> allDetails = new ArrayList<>();
//    static ArrayList<String> allPhoneNumbers = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    private String tempCategory, tempDetails, tempPhoneNumber, tempAddress;
    private String allTempCategory, allTempDetails, allTempPhoneNumber;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase, allFirebaseDatabase;
    private DatabaseReference databaseReference, demoRef, allDatabaseReference, allDemoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_issues);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView userIssuesListView = findViewById(R.id.userIssuesListView);
        categories.clear();
        details.clear();
        phoneNumbers.clear();
        address.clear();

//        allCategories.clear();
//        allDetails.clear();
//        allPhoneNumbers.clear();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        allFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        allDatabaseReference = allFirebaseDatabase.getReference();
        demoRef = databaseReference.child("Cases");
        allDemoRef = allDatabaseReference.child("Cases");

//        allDemoRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserCase userCase = snapshot.getValue(UserCase.class);
//                    allTempCategory = userCase.getCategory();
//                    allTempDetails = userCase.getDetails();
//                    allTempPhoneNumber = userCase.getPhoneNumber();
//
//                    allCategories.add(allTempCategory);
//                    allDetails.add(allTempDetails);
//                    allPhoneNumbers.add(allTempPhoneNumber);
//
//                    demoRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            categories.clear();
//                            details.clear();
//                            phoneNumbers.clear();
//
//                            for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                                UserCase tempUserCase = userSnapshot.getValue(UserCase.class);
//                                tempCategory = tempUserCase.getCategory();
//                                tempDetails = tempUserCase.getDetails();
//                                tempPhoneNumber = tempUserCase.getPhoneNumber();
//
//                                if(!tempCategory.equals("Test") && !tempDetails.equals("Test") && !tempPhoneNumber.equals("123456789")){
//                                    categories.add(tempCategory);
//                                    details.add(tempDetails);
//                                    phoneNumbers.add(tempPhoneNumber);
//                                }
//
//                                if(!allCategories.contains(categories) && !allDetails.contains(details) && !allPhoneNumbers.contains(phoneNumbers)){
//                                    userSnapshot.getRef().removeValue();
//                                }
//
//                                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
//                                userIssuesListView.setAdapter(arrayAdapter);
//
//                                userIssuesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        tempCategory = categories.get(position);
//                                        tempDetails = details.get(position);
//                                        tempPhoneNumber = phoneNumbers.get(position);
//
//                                        Intent intent = new Intent(getApplicationContext(), DetailsIssueForUserActivity.class);
//                                        intent.putExtra("category",tempCategory);
//                                        intent.putExtra("details", tempDetails);
//                                        intent.putExtra("phoneNumber", tempPhoneNumber);
//                                        finish();
//                                        startActivity(intent);
//                                    }
//                                });
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        demoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserCase userCase = snapshot.getValue(UserCase.class);
                    String tempCategory = userCase.getCategory();
                    String tempDetails = userCase.getDetails();
                    String tempPhoneNumber = userCase.getPhoneNumber();
                    String tempAddress = userCase.getAddress();

                    if(!tempCategory.equals("Test") && !tempDetails.equals("Test") && !tempPhoneNumber.equals("123456789") && !tempPhoneNumber.equals("Test")){
                        categories.add(tempCategory);
                        details.add(tempDetails);
                        phoneNumbers.add(tempPhoneNumber);
                        address.add(tempAddress);
                    }
                }

                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
                userIssuesListView.setAdapter(arrayAdapter);

                userIssuesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tempCategory = categories.get(position);
                        tempDetails = details.get(position);
                        tempPhoneNumber = phoneNumbers.get(position);
                        tempAddress = address.get(position);

                        Intent intent = new Intent(getApplicationContext(), DetailsIssueForUserActivity.class);
                        intent.putExtra("category",tempCategory);
                        intent.putExtra("details", tempDetails);
                        intent.putExtra("phoneNumber", tempPhoneNumber);
                        intent.putExtra("address", tempAddress);
                        finish();
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.userissue_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.newCase:
                startActivity(new Intent(getApplicationContext(), NewCaseActivity.class));
                break;
            case android.R.id.home:
                finish();
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
}
