package com.example.ridesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.gms.common.api.Status; // Add this import statement
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;



public class RiderHomeScreen extends AppCompatActivity {

    RiderHomeScreen.WebService temp;
    private Handler handler = new Handler();
    private Runnable runnable;
    TextView stView;
    private ConstraintLayout mainlayout;
    Button bookBtn;
    String pickupPlace,dropoffPlace;
    String pickupLat,pickupLong,dropLat,dropLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home_screen);
        Places.initialize(getApplicationContext(), "AIzaSyDTHE0iyVZ7VQdBxpz-vs3S0TjHFRCg-AA");

        AutocompleteSupportFragment pickupFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.pickup_fragment);

        AutocompleteSupportFragment dropoffFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.dropoff_fragment);

        pickupFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        dropoffFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        stView = findViewById(R.id.statusView);
        mainlayout = findViewById(R.id.fragmentLayout);
        bookBtn = findViewById(R.id.book);

        pickupFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place
                pickupPlace = place.getName();
                LatLng latLng = place.getLatLng();
                pickupLat = String.valueOf(latLng.latitude);
                pickupLong = String.valueOf(latLng.longitude);
                //String placeAddress = place.getAddress();
                // ...
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
            }
        });

        dropoffFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                dropoffPlace  = place.getName();
                LatLng latLng = place.getLatLng();
                dropLat  = String.valueOf(latLng.latitude);
                dropLong = String.valueOf(latLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        String[] parameters = new String[3];
        parameters[0] = "1";

        runnable = new Runnable() {
            @Override
            public void run() {
                temp = new RiderHomeScreen.WebService();
                System.out.println("Hiii");
                temp.execute(parameters);
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);


    }

    private class WebService extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonResponse = null;
            try {
                if(result != null){
                    jsonResponse = new JSONObject(result);
                    int statusCode = jsonResponse.getInt("statusCode");
                    String responseBody = jsonResponse.getString("body");

                    JSONObject bodyJson = new JSONObject(responseBody);
                    //String driverId = bodyJson.getJSONObject("DriverId").getString("S");
                    Boolean driverStatus = bodyJson.getJSONObject("Status").getBoolean("BOOL");
                    stView.setText(driverStatus.toString());
                    if(driverStatus){
                        bookBtn.setVisibility(View.VISIBLE);
                        mainlayout.setVisibility(LinearLayout.VISIBLE);
                    }else{
                        mainlayout.setVisibility(LinearLayout.GONE);
                        bookBtn.setVisibility(View.GONE);
                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }


        @Override
        protected String doInBackground(String... inputs) {
            String apiUrl = "https://36dpdol4tg.execute-api.us-east-1.amazonaws.com/prod/rideservice/getdriverlocation";
            JSONObject json = new JSONObject();
            String driverId = inputs[0];

            try {
                json.put("driverId", driverId);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }



}
