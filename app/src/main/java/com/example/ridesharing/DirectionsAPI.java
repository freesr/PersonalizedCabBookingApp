package com.example.ridesharing;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectionsAPI {

//    private static final String API_KEY = "AIzaSyDTHE0iyVZ7VQdBxpz-vs3S0TjHFRCg-AA";
//
//    static String[] getDistanceAndTime(String pickupLatitude, String pickupLongitude, String dropoffLatitude, String dropoffLongitude) {
//        String[] parameters = new String[4];
//        parameters[0] = pickupLatitude;
//        parameters[1] = pickupLongitude;
//        parameters[2] = dropoffLatitude;
//        parameters[3] = dropoffLongitude;
//        DirectionsAPI.
//
//        return null;
//    }
//
//    @Override
//    protected String doInBackground(String... strings) {
//
//        try {
//            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" +
//                    pickupLatitude + "," + pickupLongitude +
//                    "&destination=" + dropoffLatitude + "," + dropoffLongitude +
//                    "&mode=driving&key=" + API_KEY);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//
//            if (conn.getResponseCode() == 200) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                reader.close();
//
//                JSONObject jsonResponse = new JSONObject(response.toString());
//                JSONArray routesArray = jsonResponse.getJSONArray("routes");
//                if (routesArray.length() > 0) {
//                    JSONObject routeObject = routesArray.getJSONObject(0);
//                    JSONArray legsArray = routeObject.getJSONArray("legs");
//                    if (legsArray.length() > 0) {
//                        JSONObject legObject = legsArray.getJSONObject(0);
//                        JSONObject distanceObject = legObject.getJSONObject("distance");
//                        String distanceText = distanceObject.getString("text");
//                        JSONObject durationObject = legObject.getJSONObject("duration");
//                        String durationText = durationObject.getString("text");
//
//                        return new String[]{distanceText, durationText};
//                    }
//                }
//            }
//
//            conn.disconnect();
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}

