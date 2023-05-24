package com.example.ridesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


import org.json.JSONException;
import org.json.JSONObject;


public class DriverMainScreen extends AppCompatActivity implements LocationListener,View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_LOCATION = 1001;
    private LocationManager locationManager;
    String[] parameters = new String[4];
    String[] newparameters = new String[2];
    private boolean isTaskRunning = false;
    private Button riderScreen,detailsScreen;
    Switch status;
    Boolean statusState = false;
    String rideId;

    DriverMainScreen.WebService temp;
    DriverMainScreen.StatusUpdateWebService temp2;
    Location liveLocation;
    private Handler handler = new Handler();
    private Runnable runnable;
    String driverStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_driver_main_screen);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        parameters[0] = "1";
        newparameters[0] = "1";
        riderScreen =findViewById(R.id.rider);
        detailsScreen = findViewById(R.id.details);
        detailsScreen.setOnClickListener(this);
//        riderScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DriverMainScreen.this, RiderMapScreen.class);
//
//                startActivity(intent);
//            }
//        });



        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        status = findViewById(R.id.statusSwitch);
        statusState = status.isChecked();

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(DriverMainScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverMainScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Request the permission if it hasn't been granted
                        ActivityCompat.requestPermissions(DriverMainScreen.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                        return;
                    }

                    // If permission is already granted, request location updates
                    if (ActivityCompat.checkSelfPermission(DriverMainScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverMainScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, DriverMainScreen.this);
                    driverStatus = "Online";
                } else {
                    locationManager.removeUpdates(DriverMainScreen.this);
                    driverStatus = "Offline";


                }
                newparameters[1] = driverStatus;
                temp2 = new DriverMainScreen.StatusUpdateWebService();
                temp2.execute(newparameters);
            }
        });


        runnable = new Runnable() {
            @Override
            public void run() {
                if(liveLocation !=null){
                    parameters[1] = String.valueOf(liveLocation.getLatitude());
                    parameters[2] = String.valueOf(liveLocation.getLongitude());
                        temp = new DriverMainScreen.WebService();
                        temp.execute(parameters);


                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //updateLocation("1",location.getLatitude(),location.getLongitude());
//        if (temp != null && temp.getStatus() == AsyncTask.Status.RUNNING) {
//            temp.cancel(true);
//        }
        liveLocation = location;


//        temp  = new DriverMainScreen.WebService();
//        temp.execute(parameters);
    }

//    public void updateLocation(String driverId, double latitude, double longitude) {
//        // Create a credentials provider
//
//    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.details){
            Intent ni = new Intent(this,RideDetails.class);
            ni.putExtra("rideId",rideId);
            startActivity(ni);
        }
    }

    private class WebService extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // Process the result as needed
            isTaskRunning = false;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONObject bodyObject = new JSONObject(jsonObject.getString("body"));
                JSONObject itemObject = bodyObject.getJSONObject("Item");
                driverStatus = itemObject.getString("DriverStatus");
                rideId = itemObject.getString("LiveRideId");
                if(driverStatus.equals("OnRide")){
                    detailsScreen.setVisibility(View.VISIBLE);
                }else{
                    detailsScreen.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }


        @Override
        protected String doInBackground(String... inputs) {
            String apiUrl = "https://36dpdol4tg.execute-api.us-east-1.amazonaws.com/prod/rideservice/updatelocation";
            JSONObject json = new JSONObject();
            String driverId = inputs[0];
            String latitude = inputs[1];
            String longitude = inputs[2];

            try {
                json.put("driverId", driverId);
                json.put("latitude", latitude);
                json.put("longitude", longitude);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private class StatusUpdateWebService extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // Process the result as needed
            isTaskRunning = false;



        }


        @Override
        protected String doInBackground(String... inputs) {
            String apiUrl = "https://36dpdol4tg.execute-api.us-east-1.amazonaws.com/prod/rideservice/updatedriverstatus";
            JSONObject json = new JSONObject();
            String driverId = inputs[0];
            String driverstatus = inputs[1];

            try {
                json.put("driverId", driverId);
                json.put("driverstatus", driverstatus);

                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}

