package com.example.ridesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
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
    private static final double BASE_FARE = 5.0;  // Base fare in your currency
    private static final double DISTANCE_RATE = 0.6;  // Distance rate per kilometer or mile
    private static final double DURATION_RATE = 0.2;  // Duration rate per minute
    RiderHomeScreen.WebService temp;
    RiderHomeScreen.NewWebService temp2;
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
        String[] newparameters = new String[3];


        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] rideCal = DirectionsAPI.getDistanceAndTime(pickupLat,pickupLong,dropLat,dropLong);
                double ridePrice = calculateTripPrice(Double.parseDouble(rideCal[0]),Integer.parseInt(rideCal[1]));
                newparameters[0] = pickupLat;
                newparameters[1] = pickupLong;
                newparameters[2] = dropLat;
                newparameters[3] = dropLong;
                newparameters[4] = String.valueOf(ridePrice);
                newparameters[5] = pickupPlace;
                newparameters[6] = dropoffPlace;
                newparameters[7] = rideCal[0];
                newparameters[8] = rideCal[1];

                temp2 = new RiderHomeScreen.NewWebService();
                System.out.println("Noo ");
                temp2.execute(newparameters);
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

    private void showNextPage(){
        Intent intent = new Intent(this, RiderMapScreen.class);
        startActivity(intent);
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
                    if(statusCode == 200){
                        showNextPage();
                    }
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


            try {
                json.put("username", inputs[0]);
                json.put("pickupLatitude", inputs[1]);
                json.put("pickupLongitude", inputs[2]);
                json.put("dropoffLatitude", inputs[3]);
                json.put("dropoffLongitude", inputs[4]);
                json.put("pickupPlace", inputs[5]);
                json.put("dropoffPlace", inputs[6]);
                json.put("estimatedTime", inputs[7]);
                json.put("rideDistance", inputs[8]);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    private class NewWebService extends AsyncTask<String,Void,String> {

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
            String apiUrl = "https://36dpdol4tg.execute-api.us-east-1.amazonaws.com/prod/rideservice/bookride";
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

    static double calculateTripPrice(double distance, int duration) {
        double distanceFare = distance * DISTANCE_RATE;
        double durationFare = duration * DURATION_RATE;
        double totalFare = BASE_FARE + distanceFare + durationFare ;

        // Round the fare to 2 decimal places
        totalFare = Math.round(totalFare * 100.0) / 100.0;

        return totalFare;
    }


}
