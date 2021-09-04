package com.example.android.commuto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegistrationActivity extends AppCompatActivity {

    public static String EXTRA_PHONE = "driver_phone_number";

    private EditText driverPhoneNumber;
    private Button getCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);


        driverPhoneNumber = (EditText) findViewById(R.id.activity_driver_registration_phone_number);

        getCode = (Button) findViewById(R.id.activity_driver_registration_getCode_button);


        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String phoneNumber = driverPhoneNumber.getText().toString();

                if (phoneNumber.length() == 0) {
                    driverPhoneNumber.setError("Phone number is required");
                    driverPhoneNumber.requestFocus();
                } else if (phoneNumber.length() < 10) {
                    driverPhoneNumber.setError("Please enter a valid phone number");
                    driverPhoneNumber.requestFocus();
                } else {
                    Intent i = new Intent(DriverRegistrationActivity.this, DriverProfileActivity.class);
                    i.putExtra(EXTRA_PHONE, phoneNumber);
                    startActivity(i);

                }
            }
        });
    }
}


