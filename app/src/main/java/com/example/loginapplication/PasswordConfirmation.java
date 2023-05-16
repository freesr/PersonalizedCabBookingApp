package com.example.loginapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PasswordConfirmation extends AppCompatActivity implements View.OnClickListener {

    private EditText code,username,newpassword,renewpassword;
    private static final String API_FORGOT_PASSWORD = "https://ia219vugx9.execute-api.us-east-1.amazonaws.com/production/user/confirmforgotpassword";
    private Button restPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirmation);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        code = findViewById(R.id.userCode);
        newpassword = findViewById(R.id.newpassword);
        username = findViewById(R.id.userName);
        restPass = findViewById(R.id.submit_new_password);
        renewpassword = findViewById(R.id.reenternewpassword);
        restPass.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_new_password:
                String username1 = username.getText().toString();
                String password1 = newpassword.getText().toString();
                String repassword1 = renewpassword.getText().toString();
                String email_code = code.getText().toString();
                if(username1.equals("") ||  password1.equals("") ||  email_code.equals("") ||  repassword1.equals("")){
                    Toast.makeText(this, "UserName or password or Code is empty", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(!password1.equals(repassword1)){
                    Toast.makeText(this, "Password Dosn't match", Toast.LENGTH_SHORT).show();
                    break;
                }
                String[] paramenters = new String[4];
                paramenters[0] = username1;
                paramenters[1] = password1;
                paramenters[2] = email_code;
                paramenters[3] = API_FORGOT_PASSWORD;
                PasswordConfirmation.WebService temp = new PasswordConfirmation.WebService();
                temp.execute(paramenters);
                break;
        }
    }

    private void shouLoginPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
                String toastMsg = "Password Reset Succesful";
                if(jsonobj.getString("message").equals("Success")){
                    System.out.println("Hi   Success");
                    Toast.makeText(PasswordConfirmation.this, toastMsg, Toast.LENGTH_SHORT).show();
                    shouLoginPage();
                }else{
                    toastMsg = ApiConnection.sendToastError(jsonbd.getString("data"));
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        protected String doInBackground(String... inputs) {
            JSONObject json = new JSONObject();
            try {
                String username = inputs[0];
                String password = inputs[1];
                String email_code = inputs[2];
                String apiUrl = inputs[3];

                json.put("username",username);
                json.put("password",password);
                json.put("code",email_code);
                String jsonstring = json.toString();
                return ApiConnection.sendRequest(jsonstring, apiUrl);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}