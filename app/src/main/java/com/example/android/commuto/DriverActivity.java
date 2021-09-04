package com.example.android.commuto;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class DriverActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    public DataSnapshot dataSnapshot;

    public static String EXTRA_NUMBER = "driver_phone_number";
    private EditText phone;
    private EditText password;
    private Button signInButton;
    private Button driverRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

      // if(user != null) {
        //    Intent i = new Intent(DriverActivity.this, DriverMapActivity.class);
         //   startActivity(i);
       // }

        phone = (EditText) findViewById(R.id.activity_driver_phone_number);
        password = (EditText) findViewById(R.id.activity_driver_password);

        signInButton = (Button) findViewById(R.id.activity_driver_signIn_button);
        driverRegister = (Button) findViewById(R.id.driver_register_button);

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
                                resolvableApiException.startResolutionForResult(DriverActivity.this, 1);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });




        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                firebaseDatabase = FirebaseDatabase.getInstance();

                databaseReference = firebaseDatabase.getReference("Users").child("Drivers").child(user.getUid()).child("Password");

                final String phone_number = "+91" + phone.getText().toString();
                final String verifyPassword = password.getText().toString();

                SharedPreferences settings = getSharedPreferences("PREFS", 0);
                final  String Password = settings.getString("password", "");


                if(phone_number.equals(user.getPhoneNumber()) && verifyPassword.equals(Password)) {
                    Toast.makeText(DriverActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(DriverActivity.this, DriverMapActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(DriverActivity.this, "Sign in unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });

        driverRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverActivity.this, DriverRegistrationActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {

        }
    }

}
