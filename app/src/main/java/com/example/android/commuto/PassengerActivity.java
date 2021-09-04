package com.example.android.commuto;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

public class PassengerActivity extends AppCompatActivity {

   public static String EXTRA_PHONE = "passenger_phone_number";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private EditText passengerPhoneNumber;
    private Button getCode;
    private Dialog dialog;
    private String phoneNumber;

    final static int LOCATION_SETTINGS_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
            }
        };

        user = mAuth.getCurrentUser();

       /** if(user != null) {
            Intent intent = new Intent(PassengerActivity.this, PassengerMapActivity.class);
            startActivity(intent);
        }   **/

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest());
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                                resolvableApiException.startResolutionForResult(PassengerActivity.this, 1);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });


        passengerPhoneNumber = (EditText) findViewById(R.id.activity_passenger_registration_phone_number);

        getCode = (Button) findViewById(R.id.activity_passenger_registration_getCode_button);


        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phoneNumber = passengerPhoneNumber.getText().toString();



                if (phoneNumber.length() == 0) {
                    passengerPhoneNumber.setError("Phone number is required");
                    passengerPhoneNumber.requestFocus();
                } else if (phoneNumber.length() < 10) {
                    passengerPhoneNumber.setError("Please enter a valid phone number");
                    passengerPhoneNumber.requestFocus();
                } else {

                    sendVerificationCode();

                    dialog = new Dialog(PassengerActivity.this);
                  //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.setContentView(R.layout.dialog);
                    dialog.show();


                }
            }
        });
    }

    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PassengerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PassengerActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();

                            firebaseDatabase = FirebaseDatabase.getInstance();

                            databaseReference = firebaseDatabase.getReference("Users").child("Passengers").child(user.getUid());

                            //verification successful we will start the profile activity
                            user = mAuth.getCurrentUser();

                            databaseReference.child("Phone Number").setValue(phoneNumber);

                            Intent i = new Intent(PassengerActivity.this, PassengerMapActivity.class);
                           // i.putExtra(EXTRA_PHONE, phoneNumber);
                            startActivity(i);

                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
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


