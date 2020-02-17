package com.efulltech.efupay.e_school;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.efulltech.efupay.e_school.api.RetrofitService;
import com.efulltech.efupay.e_school.utils.AppPref;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import com.efulltech.efupay.e_school.api.RetrofitClient;
import com.efulltech.efupay.e_school.utils.Controller;

import org.efulltech.efupay.e_school.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private AppPref appPref;
    private static Context mContext;
    private Controller controller;
    EditText mEmail;
    EditText mPassword;
    Button mSignInButton;
    private LottieAlertDialog logInOperationProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        controller = new Controller(this);
        appPref = new AppPref(mContext);


        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mSignInButton = findViewById(R.id.signInButton);

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Validate();

            }
        });
    }

    private boolean Validate() {
        /* Validate all required edit text */
        // check if username is not null
        // check if password is not null
        // check if res is true;;
        boolean check = true;
        if (hasText(mEmail)) {
            if (hasText(mPassword)) {
                //        here we are validating if the fields are correctly filled
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
                controller.showOperationsDialog("Attempting Login", "Please wait...");
                /* retrofit  create instance */
                RetrofitService apiService = RetrofitClient.getClient().create(RetrofitService.class);
                //call retrofit
                Call<okhttp3.ResponseBody> call = apiService.schoolLogin(email, password);
                call.enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                        Log.d("Response string", String.valueOf(response));
                        if (response.isSuccessful()) {
                            controller.dismissProgressDialog();
                            controller.showSuccessMessage("Success", "Your login was successful", res ->{
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                Log.d("Response 36", response.message());
                            });

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

                                    //get token and to string
                                     obj.getString("access_token");
                                    Log.d("Response body 5", obj.getString("access_token"));

//                            save in shared preference
                                    AppPref appPref = new AppPref(mContext);
                                    appPref.setStringValue("token", obj.getString("access_token") );
                                     Log.d("myToken", appPref.getStringValue("token"));
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
                            controller.dismissProgressDialog();
                            controller.showErrorMessage("Authentication Error", "Error handling login credentials", null);
                            Toast.makeText(Login.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                            Log.d("Tag", "response 33: " + "no value");

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                controller.dismissProgressDialog();

                    }
                });


            } else {
                check = false;
            }
        }else {
            check = false;
        }
        return check;
    }




    private void userLogin() {

//        here we are validating if the fields are correctly filled
//        String email = mEmail.getText().toString().trim();
//        String password = mPassword.getText().toString().trim();
//
//        if (email.isEmpty()) {
//            mEmail.setError("Email is required");
//            mEmail.requestFocus();
//            return;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            mEmail.setError("Enter a valid email");
//            mEmail.requestFocus();
//            return;
//        }
//
//        if (password.isEmpty()) {
//            mPassword.setError("Password required");
//            mPassword.requestFocus();
//            return;
//        }

    }


    /* set edit text to error when empty */
    public static boolean hasText(EditText editText) {

        return hasText(editText, "Required");
    }

    /* check edit text length and set error message for required edit text
     * Custom Message */
    public static boolean hasText(EditText editText, String error_message) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(error_message);
            return false;
        }

        return true;
    }



}
