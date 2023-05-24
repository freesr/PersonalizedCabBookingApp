package com.example.ridesharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class RideDetails extends AppCompatActivity implements View.OnClickListener{

    String rideId;
    TextView location,price;
    Button pgmapsBtn,dgmapsBtn,tripEndBtn;
    String rideDistance,pickupLongitude,dropoffLatitude,dropoffLongitude,dropoffPlace,pickupLatitude,
            estimatedTime,ridePrice,pickupPlace;

    RideDetails.RideDetailsWebService temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_ride_details);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rideId = extras.getString("rideId");
        }

        location = findViewById(R.id.location);
        price = findViewById(R.id.price);
        pgmapsBtn = findViewById(R.id.pmapBtn);
        dgmapsBtn = findViewById(R.id.dmapBtn);
        dgmapsBtn.setOnClickListener(this);
        pgmapsBtn.setOnClickListener(this);
        temp = new RideDetails.RideDetailsWebService();
        temp.execute(rideId);
        tripEndBtn = findViewById(R.id.endTrip);






    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==R.id.pmapBtn){
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + pickupLatitude + "," + pickupLongitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
        if(v.getId() ==R.id.dmapBtn){
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + dropoffLatitude + "," + dropoffLongitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }


    private class RideDetailsWebService extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonResponse = null;
            try {
                if(result != null){
                    JSONObject responseJson = new JSONObject(result);
                    JSONObject bodyObject = new JSONObject(responseJson.getString("body"));
                    JSONObject firstItem = bodyObject.getJSONArray("Items").getJSONObject(0);

                    pickupPlace = firstItem.getJSONObject("PickupPlace").getString("S");
                     ridePrice = firstItem.getJSONObject("RidePrice").getString("S");
                     estimatedTime = firstItem.getJSONObject("EstimatedTime").getString("S");
                     pickupLatitude = firstItem.getJSONObject("PickupLatitude").getString("S");
                     dropoffPlace = firstItem.getJSONObject("DropoffPlace").getString("S");
                     dropoffLongitude = firstItem.getJSONObject("DropoffLongitude").getString("S");
                     dropoffLatitude = firstItem.getJSONObject("DropoffLatitude").getString("S");
                     pickupLongitude = firstItem.getJSONObject("PickupLongitude").getString("S");
                     rideDistance = firstItem.getJSONObject("RideDistance").getString("S");

                     location.setText(pickupPlace);
                     price.setText(ridePrice);
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }


        @Override
        protected String doInBackground(String... inputs) {
            String apiUrl = "https://36dpdol4tg.execute-api.us-east-1.amazonaws.com/prod/rideservice/getridedetails";
            JSONObject json = new JSONObject();

            try {
                json.put("rideId", inputs[0]);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}