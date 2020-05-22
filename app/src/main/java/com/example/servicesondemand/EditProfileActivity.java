package com.example.servicesondemand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private TextView profileInfoTextView, userNameInfoTextView, emailInfoTextView;
    private ImageView profileImageView;
    private EditText newUserNameEditText, newEmailEditText;
    private Button updateButton, cancelButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, finalDatabaseReference;
    private FirebaseUser firebaseUser;

    private String currentEmail, currentUsername, currentImageUrl;

    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;;

    private void setupUIView(){
        profileInfoTextView = findViewById(R.id.profileInfoTextView);
        userNameInfoTextView = findViewById(R.id.userNameInfoTextView);
        emailInfoTextView = findViewById(R.id.emailInfoTextView);
        profileImageView = findViewById(R.id.profileImageView);
        newUserNameEditText = findViewById(R.id.newUserNameEditText);
        newEmailEditText = findViewById(R.id.newEmailEditText);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.cancelButton);
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

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        finalDatabaseReference = databaseReference.child("UserProfile");

        finalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUserNameEditText.setText(userProfile.getUsername());
                newEmailEditText.setText(userProfile.getEmail());
                if(userProfile.getImageURL().equals("default")){
                    profileImageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(EditProfileActivity.this).load(userProfile.getImageURL()).into(profileImageView);
                }
                currentEmail = userProfile.getEmail();
                currentUsername = userProfile.getUsername();
                currentImageUrl = userProfile.getImageURL();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String username = newUserNameEditText.getText().toString();
                    final String email = newEmailEditText.getText().toString();

                    UserProfile userProfile = new UserProfile(email, username, currentImageUrl);

                    finalDatabaseReference.setValue(userProfile);
                    if(!currentEmail.equals(email)){
                        firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "Profile information updated successfully! Make sure that you have verified your email before next login!",Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }
                                    });
//                                Toast.makeText(getApplicationContext(), "Profile information updated successfully!",Toast.LENGTH_SHORT).show();
//                                finish();
                                }else {
                                    Toast.makeText(getApplicationContext(), "Profile information update failure!",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    } else if (currentEmail.equals(email) && !currentUsername.equals(username)){
                        Toast.makeText(getApplicationContext(), "Profile information updated successfully!",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "No changes made!",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        String mUri = downloadUrl.toString();

                        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
                        finalDatabaseReference = databaseReference.child("UserProfile");
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);

                        finalDatabaseReference.updateChildren(map);

                        Toast.makeText(getApplicationContext(), "Profile picture changed successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(), "Upload in progress!", Toast.LENGTH_SHORT).show();
            } else{
                uploadImage();
            }
        }

    }

}
