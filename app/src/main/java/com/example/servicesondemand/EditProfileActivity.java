package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private TextView profileInfoTextView, userNameInfoTextView, emailInfoTextView;
    private ImageView profileImageView;
    private EditText newUserNameEditText, newEmailEditText;
    private Button updateButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, finalDatabaseReference;
    private FirebaseUser firebaseUser;

    private void setupUIView(){
        profileInfoTextView = findViewById(R.id.profileInfoTextView);
        userNameInfoTextView = findViewById(R.id.userNameInfoTextView);
        emailInfoTextView = findViewById(R.id.emailInfoTextView);
        profileImageView = findViewById(R.id.profileImageView);
        newUserNameEditText = findViewById(R.id.newUserNameEditText);
        newEmailEditText = findViewById(R.id.newEmailEditText);
        updateButton = findViewById(R.id.updateButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupUIView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        finalDatabaseReference = databaseReference.child("UserProfile");

        finalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUserNameEditText.setText(userProfile.getUsername());
                newEmailEditText.setText(userProfile.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String username = newUserNameEditText.getText().toString();
                    String email = newEmailEditText.getText().toString();

                    UserProfile userProfile = new UserProfile(email, username);

                    finalDatabaseReference.setValue(userProfile);
                    firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Profile information updated successfully!",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "Profile information update failure!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private Boolean validate(){
        Boolean result = false;

        String username = newUserNameEditText.getText().toString();
        String email = newEmailEditText.getText().toString();

        if(username.isEmpty() || email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter all the details!",Toast.LENGTH_SHORT).show();
        }else {
            result = true;
        }

        return result;
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
