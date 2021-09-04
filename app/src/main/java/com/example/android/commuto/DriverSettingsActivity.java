package com.example.android.commuto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DriverSettingsActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private Uri resultUri;
    private String mProfileImageUrl;

    private ListView imageList;

    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private EditText place;

    private TextView verifiedOrNot;

    private Button save;
    private Button cancel;


    private DatabaseReference driverDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;
    String user_id;

    private String driverFirstName, driverLastName, driverPhoneNumber, driverEmail, newPhoneNumber, driverPassword, driverPlace;

    private Dialog dialog;

    private boolean taskBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        user_id = user.getUid();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
            }
        };

        driverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);

        getUserInfo();

        mProfileImage = (ImageView) findViewById(R.id.driverImage);
        imageList = (ListView) findViewById(R.id.pick_image_listView);

        firstName = (EditText) findViewById(R.id.driver_FirstName);
        lastName = (EditText) findViewById(R.id.driver_LastName);
        phoneNumber = (EditText) findViewById(R.id.driver_phoneNumber);
        password = (EditText) findViewById(R.id.driver_Password);
        place = (EditText) findViewById(R.id.driver_place) ;
        email = (EditText) findViewById(R.id.driver_Email);

        verifiedOrNot = (TextView) findViewById(R.id.verified);


        cancel = (Button) findViewById(R.id.cancelButton_activity_driver_settings);

        //  phoneNumber.setText(phone);

        dialog = new Dialog(DriverSettingsActivity.this);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
            }
        });

        save = (Button) findViewById(R.id.saveButton_activity_driver_settings);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                finish();
                return;


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

    }

    private void getUserInfo() {
        driverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0 ) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("First Name") != null) {
                        driverFirstName = map.get("First Name").toString();
                        firstName.setText(driverFirstName);
                    }
                    if(map.get("Last Name") != null) {
                        driverLastName = map.get("Last Name").toString();
                        lastName.setText(driverLastName);
                    }
                    if(map.get("Phone Number") != null) {
                        driverPhoneNumber = map.get("Phone Number").toString();
                        phoneNumber.setText(driverPhoneNumber);
                    }
                    if(map.get("Password") != null) {
                        driverPassword = map.get("Password").toString();
                        password.setText(driverPassword);
                    }
                    if(map.get("Place") != null) {
                        driverPlace = map.get("Place").toString();
                        place.setText(driverPlace);
                    }
                    if(map.get("Email") != null) {
                        driverEmail = map.get("Email").toString();
                        email.setText(driverEmail);
                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        driverFirstName = firstName.getText().toString();
        firstName.setText(driverFirstName);
        driverDatabase.child("First Name").setValue(driverFirstName);


        driverLastName = lastName.getText().toString();
        lastName.setText(driverLastName);
        driverDatabase.child("Last Name").setValue(driverLastName);

        driverPassword = password.getText().toString();
        password.setText(driverPassword);
        driverDatabase.child("Password").setValue(driverPassword);

        driverPlace = place.getText().toString();
        place.setText(driverPlace);
        driverDatabase.child("Place").setValue(driverPlace);

        driverEmail = email.getText().toString();
        email.setText(driverEmail);
        driverDatabase.child("Email").setValue(driverEmail);

        newPhoneNumber = phoneNumber.getText().toString();

        if(newPhoneNumber.length() < 10) {
            phoneNumber.setError("Please enter a valid phone number");
            phoneNumber.requestFocus();
        }
        if(!newPhoneNumber.equals(driverPhoneNumber) && newPhoneNumber.length() == 10) {
            dialog.setContentView(R.layout.dialog);
            dialog.show();
            driverPhoneNumber = newPhoneNumber;
            sendVerificationCode();
        }

        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(user_id);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    driverDatabase.child("profileImageUrl").setValue(downloadUrl.toString());
                    finish();
                    return;
                }
            });
        }else{
            finish();
        }
    }

    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + driverPhoneNumber,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(DriverSettingsActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            phoneNumber.setText(driverPhoneNumber);
                            verifiedOrNot.setText("verified");
                            dialog.dismiss();
                            driverDatabase.child("Phone Number").setValue(driverPhoneNumber);
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

}
