package org.example.orafucharles.e_school;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.example.orafucharles.e_school.api.RetrofitClient;
import org.example.orafucharles.e_school.api.RetrofitService;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class Login extends AppCompatActivity {

    EditText mEmail;
    EditText mPassword;
    Button mSignInButton;
    TextView mForgotPassword;
    String success = "Login Successful";
    public SharedPreferences.Editor mEditor;
    public SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mSignInButton = findViewById(R.id.signInButton);
        mForgotPassword = findViewById(R.id.forgotPassword);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = preferences.edit();

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });
    }


    private void userLogin() {

//        here we are validating if the fields are correctly filled
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Enter a valid email");
            mEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPassword.setError("Password required");
            mPassword.requestFocus();
            return;
        }


        /* retrofit  create instance */
        RetrofitService apiService = RetrofitClient.getClient().create(RetrofitService.class);
        //call retrofit
        Call<okhttp3.ResponseBody> call = apiService.schoolLogin(email, password);
        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {

                Log.d("Response string", String.valueOf(response));

                if (response.isSuccessful()) {
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, WorkManager.class);
                    startActivity(intent);

                    Log.d("Response 36", response.message());

                    //the response body is already parse able to your response body object
                    ResponseBody responseBody = response.body();
                    //response body string is null
                    String responseBodyString = null;
                    try {
                        assert responseBody != null;
                        responseBodyString = responseBody.string();

                        try {
                            JSONObject obj = new JSONObject(responseBodyString);
                            Log.d("Response body", String.valueOf(obj));

                            //get token to string
                            String token = obj.getString("access_token");
                            Log.d("Response body 5", token);

//                            save in shared preference
                            mEditor.putString("token", token);
                            mEditor.apply();
//                            Toast.makeText(Login.this, token, Toast.LENGTH_SHORT).show();
                            String mToken = preferences.getString("token", "");
                            Log.d("shared", mToken);
//                            Toast.makeText(Login.this, mToken, Toast.LENGTH_LONG).show();

                            //get user to string
                            String user = obj.getString("message");
                            Log.d("Response body 6", user);
                            JSONObject obj2 = new JSONObject(user);


                            //get name to string
                            String name = obj2.getString("name");
                            Log.d("Response body name", name);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Login.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                    Log.d("Tag", "response 33: " + "no value");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}
