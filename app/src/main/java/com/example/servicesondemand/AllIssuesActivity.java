package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class AllIssuesActivity extends AppCompatActivity {

    private ListView issuesListView;

    static ArrayList<String> categories = new ArrayList<>();
    static ArrayList<String> details = new ArrayList<>();
    static ArrayList<String> phoneNumber = new ArrayList<>();
    static ArrayList<String> address = new ArrayList<>();

    static ArrayList<String> userCategories = new ArrayList<>();
    static ArrayList<String> userDetails = new ArrayList<>();
    static ArrayList<String> userPhoneNumber = new ArrayList<>();
    static ArrayList<String> userAddress = new ArrayList<>();

    static ArrayAdapter<String> arrayAdapter;

    private String tempCategory, tempDetails, tempPhoneNumber, tempAddress;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase, firebaseUserDatabase;
    private DatabaseReference databaseReference, databaseUserReference, demoRef, demoUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_issues);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        issuesListView = findViewById(R.id.issuesListView);
        categories.clear();
        details.clear();
        phoneNumber.clear();
        address.clear();

        userCategories.clear();
        userDetails.clear();
        userPhoneNumber.clear();
        userAddress.clear();

        //FireBase

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUserDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseUserReference = firebaseUserDatabase.getReference(firebaseAuth.getUid());

        demoUserRef = databaseUserReference.child("Cases");
        demoRef = databaseReference.child("Cases");

        demoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserCase userCase = snapshot.getValue(UserCase.class);
                    tempCategory = userCase.getCategory();
                    tempDetails = userCase.getDetails();
                    tempPhoneNumber = userCase.getPhoneNumber();
                    tempAddress = userCase.getAddress();

                    categories.add(tempCategory);
                    details.add(tempDetails);
                    phoneNumber.add(tempPhoneNumber);
                    address.add(tempAddress);

                    demoUserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userCategories.clear();
                            userDetails.clear();
                            userPhoneNumber.clear();
                            userAddress.clear();

                            for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                UserCase tempUserCase = userSnapshot.getValue(UserCase.class);
                                String tempUserCategory = tempUserCase.getCategory();
                                String tempUserDetails = tempUserCase.getDetails();
                                String tempUserPhoneNumber = tempUserCase.getPhoneNumber();
                                String tempUserAddress = tempUserCase.getAddress();


                                userCategories.add(tempUserCategory);
                                userDetails.add(tempUserDetails);
                                userPhoneNumber.add(tempUserPhoneNumber);
                                userAddress.add(tempUserAddress);

                                categories.removeAll(userCategories);
                                details.removeAll(userDetails);
                                phoneNumber.removeAll(userPhoneNumber);
                                address.removeAll(userAddress);

                                arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, categories);
                                issuesListView.setAdapter(arrayAdapter);

                                issuesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        tempCategory = categories.get(position);
                                        tempDetails = details.get(position);
                                        tempPhoneNumber = phoneNumber.get(position);
                                        tempAddress = address.get(position);
//                                        System.out.println(address.get(position));
//                                        System.out.println(address.size());

                                        Intent intent = new Intent(getApplicationContext(), DetailsIssueForAllActivity.class);
                                        intent.putExtra("category",tempCategory);
                                        intent.putExtra("details", tempDetails);
                                        intent.putExtra("phoneNumber", tempPhoneNumber);
                                        intent.putExtra("address", tempAddress);
                                        finish();
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
