package com.example.ridesharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InitalPage extends AppCompatActivity implements View.OnClickListener {
    private Button driverBtn,riderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inital_page);
        driverBtn = findViewById(R.id.driver);
        riderBtn = findViewById(R.id.rider);
        driverBtn.setOnClickListener(this);
        riderBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v.getId() == R.id.driver) {
            intent = new Intent(InitalPage.this, DriverMainScreen.class);
        }else if(v.getId() == R.id.rider){
            intent = new Intent(InitalPage.this, RiderHomeScreen.class);
        }
        startActivity(intent);
    }
}