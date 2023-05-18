package com.example.ridesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;


public class DriverMainScreen extends AppCompatActivity implements LocationListener {
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    String[] parameters = new String[3];
    private boolean isTaskRunning = false;

    DriverMainScreen.WebService temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main_screen);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        parameters[0] = "1";

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it hasn't been granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        // If permission is already granted, request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);




    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //updateLocation("1",location.getLatitude(),location.getLongitude());
//        if (temp != null && temp.getStatus() == AsyncTask.Status.RUNNING) {
//            temp.cancel(true);
//        }
        parameters[1] = String.valueOf(location.getLatitude());
        parameters[2] = String.valueOf(location.getLongitude());
        if (!isTaskRunning) {
            temp = new DriverMainScreen.WebService();
            temp.execute(parameters);
            isTaskRunning = true;
        }

//        temp  = new DriverMainScreen.WebService();
//        temp.execute(parameters);
    }

    public void updateLocation(String driverId, double latitude, double longitude) {
        // Create a credentials provider

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

}

