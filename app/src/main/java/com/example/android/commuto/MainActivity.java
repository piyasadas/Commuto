package com.example.android.commuto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mDriver, mPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDriver = (Button)findViewById(R.id.driver_login);
        mPassenger = (Button)findViewById(R.id.passenger_login);

        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverLoginIntent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(driverLoginIntent);
                finish();
                return;
            }
        });
    }
}
