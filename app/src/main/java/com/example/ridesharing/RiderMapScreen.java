package com.example.ridesharing;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class RiderMapScreen extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Button currentLocationBtn, plusSignBtn, minusSignBtn;
    LatLng latLng;
    int zoom = 16;
    RiderMapScreen.WebService temp;
    Marker usermarker,driverMarker;
    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main_screen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        plusSignBtn = findViewById(R.id.plusSign);
        minusSignBtn = findViewById(R.id.minusSign);
        plusSignBtn.setOnClickListener(this);
        minusSignBtn.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 8000, 0, this);
        String[] parameters = new String[3];
        parameters[0] = "1";



        runnable = new Runnable() {
            @Override
            public void run() {
                 temp = new RiderMapScreen.WebService();
                System.out.println("Hiii        ");
                temp.execute(parameters);
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.getLocBtn) {
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
            if (latLng != null) {
                setZoom();
            }
        } else if (v.getId() == R.id.plusSign) {
            if (latLng != null) {
                zoom = zoom + 3;
                setZoom();
            }
        } else if (v.getId() == R.id.minusSign) {
            zoom = zoom - 3;
            setZoom();
        }
    }

    private void setZoom() {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
//        latLng = new LatLng(39.261, -76.699);
//        mMap.addMarker(new MarkerOptions().position(latLng).title("Inital User Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        Toast.makeText(getApplicationContext(), "click current location again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        removeMarkersByType("User");

        // Add a new marker for the user's location
        usermarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                .position(latLng)
                .title("User Location")
                .snippet("Type: User"));
    }
    private void removeMarkersByType(String markerType) {

        if(markerType.equals("User")){
            if(usermarker != null){
                usermarker.remove();
            }
        } else{
            if(driverMarker != null){
                driverMarker.remove();
            }
        }

//        for (Marker marker : mMap.getMarkers()) {
//            if (marker.getSnippet() != null && marker.getSnippet().equals("Type: " + markerType)) {
//                marker.remove();
//            }
//        }
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
                    String longitude = bodyJson.getJSONObject("Longitude").getString("S");
                    String latitude = bodyJson.getJSONObject("Latitude").getString("S");
                    latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    removeMarkersByType("Driver");
                    driverMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.cab))
                            .position(latLng).title("Driver Location").snippet("Type: Driver"));
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