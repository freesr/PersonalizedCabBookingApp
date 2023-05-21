package com.example.ridesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    private LinearLayout mainlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home_screen);
        Places.initialize(getApplicationContext(), "AIzaSyCXti4hYs6ZOC4IVh6PQ3zgpWHqa0Ash9Q");

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        stView = findViewById(R.id.statusView);
        mainlayout = findViewById(R.id.fragmentLayout);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place
                String placeName = place.getName();
                String placeAddress = place.getAddress();
                // ...
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
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
                    String driverStatus = bodyJson.getJSONObject("Status").getString("S");
                    stView.setText(driverStatus);
                    if(driverStatus.equals("Online")){
                        mainlayout.setVisibility(LinearLayout.VISIBLE);
                    }else{
                        mainlayout.setVisibility(LinearLayout.GONE);
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
