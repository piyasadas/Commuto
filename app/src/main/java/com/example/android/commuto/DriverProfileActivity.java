package com.example.android.commuto;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class DriverProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;//the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(DriverProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private EditText driverFirstName;
    private EditText driverLastName;
    private EditText driverPassword;
    private EditText driverPlace;
    private EditText driverEmailAddress;
    private Button continueButton;

    private Dialog dialog;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
            }
        };

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra(DriverRegistrationActivity.EXTRA_PHONE);

        sendVerificationCode();

        dialog = new Dialog(DriverProfileActivity.this);
      //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog);
        dialog.show();


        driverFirstName = (EditText) findViewById(R.id.activity_driver_profile_firstName);

        driverLastName = (EditText) findViewById(R.id.activity_driver_profile_lastName);

        driverPassword = (EditText) findViewById(R.id.activity_driver_profile_password);

        driverPlace = (EditText) findViewById(R.id.activity_driver_profile_place);

        driverEmailAddress = (EditText) findViewById(R.id.activity_driver_profile_email);

        continueButton = (Button) findViewById(R.id.activity_driver_profile_continue_button);

        driverFirstName.setEnabled(false);
        driverLastName.setEnabled(false);
        driverPassword.setEnabled(false);
        driverPlace.setEnabled(false);
        driverEmailAddress.setEnabled(false);
        continueButton.setEnabled(false);


    }

    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(DriverProfileActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();

                            firebaseDatabase = FirebaseDatabase.getInstance();

                            databaseReference = firebaseDatabase.getReference("Users").child("Drivers").child(user.getUid());


                            //verification successful we will start the profile activity
                            user = mAuth.getCurrentUser();


                            databaseReference.child("Phone Number").setValue(phoneNumber);

                            driverFirstName.setEnabled(true);
                            driverLastName.setEnabled(true);
                            driverPassword.setEnabled(true);
                            driverPlace.setEnabled(true);
                            driverEmailAddress.setEnabled(true);
                            continueButton.setEnabled(true);

                            continueButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String firstName = driverFirstName.getText().toString();
                                    if (firstName.length() == 0) {
                                        driverFirstName.setError("First Name is required");
                                        driverFirstName.requestFocus();
                                    } else {
                                        databaseReference.child("First Name").setValue(firstName);
                                    }

                                    final String lastName = driverLastName.getText().toString();
                                    if (lastName.length() == 0) {
                                        driverLastName.setError("Last Name is required");
                                        driverLastName.requestFocus();
                                    } else {
                                        databaseReference.child("Last Name").setValue(lastName);
                                    }

                                    final String password = driverPassword.getText().toString();
                                    if (password.length() < 8 && password.length() > 0) {
                                        driverPassword.setError("Minimum 8 characters is required");
                                        driverPassword.requestFocus();
                                    }
                                    if (password.length() == 0) {
                                        driverPassword.setError("Password is required");
                                        driverPassword.requestFocus();
                                    } else {
                                        databaseReference.child("Password").setValue(password);
                                        SharedPreferences settings = getSharedPreferences("PREFS", 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("password", password);
                                        editor.apply();
                                    }

                                    final String place = driverPlace.getText().toString();
                                    if (place.length() == 0) {
                                        driverPlace.setError("City/ Town/ Village is required");
                                        driverPlace.requestFocus();
                                    } else {
                                        databaseReference.child("Place").setValue(place);
                                    }

                                    final String email = driverEmailAddress.getText().toString();
                                    if (email.length() != 0) {
                                        databaseReference.child("Email").setValue(email);
                                    }

                                    if (firstName.length() > 0 && lastName.length() > 0 &&
                                            (password.length() >= 8) && place.length() > 0) {
                                        Intent i = new Intent(DriverProfileActivity.this,
                                                DriverMapActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });

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

}
