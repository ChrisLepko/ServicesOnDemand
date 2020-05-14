package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userNameEditText, passwordEditText, emailEditText, passwordConfirmationEditText;
    private Button registerButton;
    private TextView loginTextView;
    private FirebaseAuth firebaseAuth;
    private String userName, email, password, passwordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //Upload data to the database
                    String user_email = emailEditText.getText().toString().trim();
                    String user_password = passwordEditText.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendUserData();
                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupUIViews(){
        userNameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        passwordConfirmationEditText = findViewById(R.id.passwordConfirmationEditText);
    }

    private Boolean validate(){
        Boolean result = false;

        userName = userNameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        passwordConfirmation = passwordConfirmationEditText.getText().toString();
        email = emailEditText.getText().toString();

        if(userName.isEmpty() || password.isEmpty() || email.isEmpty() || passwordConfirmation.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            if(password.equals(passwordConfirmation)){
                result = true;
            }else{
                Toast.makeText(this, "Typed passwords does not match!", Toast.LENGTH_SHORT).show();
            }
        }
        return result;
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        DatabaseReference myRefFinal = myRef.child("UserProfile");
        UserProfile userProfile = new UserProfile(email, userName);
        myRefFinal.setValue(userProfile);

        DatabaseReference demoRef = myRef.child("Cases");
        String category = "Test";
        String details = "Test";
        String phoneNumber = "123456789";
        String address = "Test";
        UserCase userCase = new UserCase(category, details, phoneNumber, address);
        demoRef.push().setValue(userCase);
    }
}
