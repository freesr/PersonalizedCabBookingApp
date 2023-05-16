package com.example.loginapplication;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ApiConnection {

    public static String sendRequest(String jsonstring, String apiUrl){

        try{
        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.getDoOutput();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
        writer.write(jsonstring);
        writer.flush();
        writer.close();

        if (urlConnection.getResponseCode() == 200) {
            BufferedReader bread = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            //bread.readLine();
            String temp, responseString = "";

            while ((temp = bread.readLine()) != null) {
                responseString += temp;
            }
            return responseString;

        }


        return null;
    } catch ( Exception e) {
        throw new RuntimeException(e);
    }
    }

    public static String sendToastError(String errorString){
        if (errorString.contains("UserNotFoundException")) {
           return  "Incorrect username or password";
        } else if (errorString.contains("UserNotConfirmedException")) {
            return "User is not confirmed.";
        } else if (errorString.contains("NotAuthorizedException")) {
            return "Incorrect username or password.";
        }  else if (errorString.contains("CodeDeliveryFailureException")) {
            return "Code Delivery Failed";
        } else if (errorString.contains("InvalidPasswordException")) {
            return "password didn't match with policy";
        } else if (errorString.contains("ExpiredCodeException")) {
            return "code has expired";
        } else if (errorString.contains("CodeMismatchException")) {
            return "code doesn't match";
        } else if (errorString.contains("UsernameExistsException")) {
            return  "User already exists";
        } else if (errorString.contains("InvalidParameterException")) {
            return  "Invalid email address format";
        } else {
            return "Internal Error";
        }
    }
}
