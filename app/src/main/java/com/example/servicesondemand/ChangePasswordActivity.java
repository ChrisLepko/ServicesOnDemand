package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView changeInfoTextView;
    private EditText newPasswordEditText, confirmNewPassowrdEditText;
    private Button changePasswordButton;

    private String newPassword, confirmedNewPassword;

    private FirebaseUser firebaseUser;

    private void setupUIViews(){
        changeInfoTextView = findViewById(R.id.changeInfoTextView);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPassowrdEditText = findViewById(R.id.confirmNewPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setupUIViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPassword = newPasswordEditText.getText().toString().trim();
        confirmedNewPassword = confirmNewPassowrdEditText.getText().toString().trim();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Password has been changed!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "Password Update Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private Boolean validate(){

        Boolean result = false;

        newPassword = newPasswordEditText.getText().toString().trim();
        confirmedNewPassword = confirmNewPassowrdEditText.getText().toString().trim();

        if (newPassword.isEmpty() || confirmedNewPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password field cannot be empty!", Toast.LENGTH_SHORT).show();
        }else {
            if(newPassword.equals(confirmedNewPassword)){
                result = true;
            }else {
                Toast.makeText(getApplicationContext(), "Typed passwords does not match!", Toast.LENGTH_SHORT).show();
            }
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
