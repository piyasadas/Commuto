package com.example.android.commuto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    private Button driverButton;
    private Button passengerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        driverButton = (Button) findViewById(R.id.i_am_a_driver);
        passengerButton = (Button) findViewById(R.id.i_am_a_passenger);

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, DriverActivity.class);
                startActivity(intent);
            }
        });

        passengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, PassengerActivity.class);
                startActivity(intent);
            }
        });

    }
}
