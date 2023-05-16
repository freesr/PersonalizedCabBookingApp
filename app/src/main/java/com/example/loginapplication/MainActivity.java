package com.example.loginapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String API_SIGNIN = "https://ia219vugx9.execute-api.us-east-1.amazonaws.com/production/user/signin";
    private static final String API_PASSWORD_RST = "https://ia219vugx9.execute-api.us-east-1.amazonaws.com/production/user/forgotpassword";
    private Button loginBtn,signUpBtn,passwordForgot;
    private EditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login);
        passwordForgot = findViewById(R.id.passwordForgot);
        signUpBtn = findViewById(R.id.signUp);
        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        passwordForgot.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                String username1 = username.getText().toString();
                String password1 = password.getText().toString();
                if(username1.equals("") ||  password1.equals("")){
                    Toast.makeText(this, "UserName or password is empty", Toast.LENGTH_SHORT).show();
                    break;
                }
                String[] paramenters = new String[3];
                paramenters[0] = username1;
                paramenters[1] = password1;
                paramenters[2] = API_SIGNIN;
                WebService temp = new WebService();
                temp.execute(paramenters);
                break;
            case R.id.signUp:
                Intent intent = new Intent(this, Registration.class);
                startActivity(intent);
                break;
            case R.id.passwordForgot:
                passwordResetOverlay();
                break;
        }

    }

    private void showNextPage(){
        Intent intent = new Intent(this, PasswordConfirmation.class);
        startActivity(intent);
    }

    private void toastMsg(){
        Toast.makeText(this, "UserName is empty", Toast.LENGTH_SHORT).show();
    }

    private void passwordResetOverlay() {
        View overlayView = getLayoutInflater().inflate(R.layout.forgot_password, null);

        // Find the EditText and Button views in the overlay layout
        EditText user_name = overlayView.findViewById(R.id.user_name);
        Button resetBtn = overlayView.findViewById(R.id.reset_password);

        // Create a dialog to show the overlay view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(overlayView);
        AlertDialog dialog = builder.create();

        // Set an OnClickListener on the submit button to handle text submission
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String textInput = emailId.getText().toString();
                String[] paramenters = new String[2];
                paramenters[0] = user_name.getText().toString();
                if(paramenters[0].equals("")){
                    toastMsg();
                }else{
                    paramenters[1] = API_PASSWORD_RST;
                    MainActivity.WebServiceNew temp = new MainActivity.WebServiceNew();
                    temp.execute(paramenters);
                }
            }
        });

        // Show the dialog
        dialog.show();
    }

    private class WebService extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonobj = null;
            try {
                jsonobj = new JSONObject(result);
                JSONObject jsonbd = new JSONObject(jsonobj.getString("body"));
                String toastMsg = "Login Successful";
                if (jsonbd.getString("message").equals("Success")) {
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://umbc.edu/"));
                    startActivity(browserIntent);
                } else {
                    toastMsg = ApiConnection.sendToastError(jsonbd.getString("data"));
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected String doInBackground(String... inputs) {
            //String apiUrl = "https://9qcp5y13z2.execute-api.us-east-1.amazonaws.com/prod/user/signin";
            JSONObject json = new JSONObject();
            String username = inputs[0];
            String password = inputs[1];
            String apiUrl = inputs[2];

            try {
                json.put("username", username);
                json.put("password", password);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }



    private class WebServiceNew extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonobj = null;

            try {
                jsonobj = new JSONObject(result);

                JSONObject jsonbd = new JSONObject(jsonobj.getString("body"));
                String toastMsg = "type code from email id";
                if (jsonbd.getString("message").equals("Success")) {
                    String emailID = (new JSONObject(jsonbd.getString("data"))).getString("Destination");
                    Toast.makeText(MainActivity.this, toastMsg + emailID, Toast.LENGTH_SHORT).show();
                    showNextPage();
                } else {
                    toastMsg = ApiConnection.sendToastError(jsonbd.getString("data"));
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected String doInBackground(String... inputs) {
            JSONObject json = new JSONObject();
            try {
                String username = inputs[0];
                String apiUrl = inputs[1];
                json.put("username", username);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}