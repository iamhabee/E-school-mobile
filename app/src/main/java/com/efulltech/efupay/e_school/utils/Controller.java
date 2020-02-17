package com.efulltech.efupay.e_school.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.efull.smsgateway.api.http.HttpHelper;
import com.efull.smsgateway.api.json.JsonSMS;
import com.efull.smsgateway.api.json.RequestJSON;
import com.efulltech.efupay.e_school.ESchoolLoad;
import com.efulltech.efupay.e_school.Login;
import com.efulltech.efupay.e_school.MainActivity;
import com.efulltech.efupay.e_school.api.ArrayResponseCallback;
import com.efulltech.efupay.e_school.api.JsonObjectResponse;
import com.efulltech.efupay.e_school.api.ResponseCallback;
import com.efulltech.efupay.e_school.db.UserContract;
import com.efulltech.efupay.e_school.db.UserReaderDbHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.efulltech.efupay.e_school.db.UserContract.UserEntry.TABLE_NAME1;
import static com.efulltech.efupay.e_school.db.UserContract.UserEntry.TABLE_NAME2;

public class Controller {

    private  static final int TIME_OUT = 5000;
    private  static final int MSG_DISMISS_DIALOG = 0;
    private AlertDialog mAlertDialog;
    private static final String TAG = "Controller";
    private static LottieAlertDialog dialog;
    private static Context mContext;
    SharedPreferences mPreferences;
    public SharedPreferences preferences;
    SharedPreferences.Editor mEditor;
    private ProgressDialog progressDialog;
    private LottieAlertDialog studentSuccessDialog;
    UserReaderDbHelper dbHelper;
    private Gson gson = new Gson();
    SQLiteDatabase sqLiteDatabase;
    private AppPref appPref;



    public Controller(Context context, SharedPreferences preferences) {

        this.mContext = context;
        this.mPreferences = preferences;
        dbHelper = new UserReaderDbHelper(mContext);
        appPref = new AppPref(mContext);
    }

    public Controller(Context context) {
        this.mContext = context;
        dbHelper = new UserReaderDbHelper(mContext);
    }

    public void validateToken(String token, JsonObjectResponse callback) throws JSONException {
        String url = Globals.ESCHOOL_BASE_URL+"checkAuth";//this is were we set the base url and the endpoint
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("validate token", String.valueOf((response)));
                callback.done(response, null);
            }
        }, error -> {
            // TODO: Handle error
            error.printStackTrace();
            Log.d("EFU AUTH", error.toString());
            callback.done(null, error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        // set retry policy to determine how long volley should wait before resending a failed request
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // add jsonObjectRequest to the queue
        requestQueue.add(jsonObjectRequest);
    }


    public void getStudentId(String id, JsonObjectResponse callback) throws JSONException {
        String url = Globals.ESCHOOL_BASE_URL+"auth/schoolLogin";//this is were we set the base url and the endpoint
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("validate school student", String.valueOf((response)));
                callback.done(response, null);
            }
        }, error -> {
            // TODO: Handle error
            error.printStackTrace();
            Log.d("EFU i", error.toString());
            callback.done(null, error);
        }) {
        };
        // set retry policy to determine how long volley should wait before resending a failed request
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // add jsonObjectRequest to the queue
        requestQueue.add(jsonObjectRequest);
    }

    public void checkIfUserHasLoggedInBefore() throws JSONException {
        AppPref appPref = new AppPref(mContext);
        String authToken = appPref.getStringValue("token");
        Integer ids = appPref.getIntValue("id");



        if (authToken == null){
            Intent intent = new Intent(mContext.getApplicationContext(), Login.class);
            mContext.startActivity(intent);
        }
        else {
            Log.d("response token", authToken);
            Log.d("studentids", String.valueOf(ids));

            this.validateToken(authToken, (response, e) ->{
                if(e == null){
                    if(response.length() > 0){
                            try {
                                if (response.getInt("response") == 200) {
                                    if(response.getBoolean("auth") == true){
                                        Log.d("response success", "response");
//                                        Log.d("student", response.getJSONObject("students").getString("id"));

                                        Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
                                        mContext.startActivity(intent);
                                    }else{
                                        // auth failed
                                        Log.d("response failedd", "response failed");
                                        Intent intent = new Intent(mContext.getApplicationContext(), Login.class);
                                        mContext.startActivity(intent);
                                    }
                                }else{
                                    // token expired, auth failed
                                    Log.d("response bad", "token expired");
                                    Intent intent = new Intent(mContext.getApplicationContext(), Login.class);
                                    mContext.startActivity(intent);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                    }
                }
            });
        }
    }

    public void queryOutgoingNotifsAndSendToApi() throws JSONException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME1 + " WHERE "
                + UserContract.UserEntry.MESSAGE_SENT+ "=?", new String[]{"false"});
        Log.d("Draft Entries", String.valueOf(cursor.getCount()));
        JSONArray smsPayload = new JSONArray();
        // if Cursor is contains results
        if (cursor != null) {
            // move cursor to first row
            if (cursor.moveToFirst()) {
                do {

                    // create a new sms object and populate sms payload
                    JSONObject sms = new JSONObject();
                    sms.put("msgId", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_ID)));
                    sms.put("datetimeSubmit", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.DATE))); // change this to current device time
                    sms.put("msgText", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_CONTENT)));
                    sms.put("msIsdn", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_ISDN)));
                    sms.put("sender", "efull");
                    smsPayload.put(sms);
                } while (cursor.moveToNext());
            }

            // check if smsPayload has any content to be sent and handle accordingly
            if(smsPayload.length() > 0){
                Log.d("SMS PAYLOAD FINAL", smsPayload.toString());
                try {
                    this.sendSms(smsPayload, (objResponse, error)->{
                        if(error == null){
                            Log.d("SMS SEND RESPONSE: ", objResponse.toString());
                            // check if responseCode is OK
                            try {
                                if(objResponse.getString("responseCode").equals("OK")){
                                    // loop through smsResponses array
                                    for(int i = 0; i < objResponse.getJSONArray("smsResponses").length(); i++){
                                        // for each sms sent, handle accordingly
                                        JSONObject entry = objResponse.getJSONArray("smsResponses").getJSONObject(i);
                                        if(entry.getBoolean("requestOk")){
                                            dbHelper.updateSmsData(entry.getString("msgId"));
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void persistSentMessagesOnTheServer(JSONArray eSchool, ArrayResponseCallback callback){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        using sharedpref to get the token of the school logged in
        Controller controller = new Controller(mContext, preferences);
        controller.getToken(mContext, (token, err) -> {//making use of the token to get the sharedpref in the controller
            if (err == null) {
                String url = Globals.ESCHOOL_BASE_URL+"sentMessageLog";//this is were we set the base url and the endpoint
                Log.d("JSON ARRAY", eSchool.toString());
//                try {

//                    using volley api to get the various parameter that would aid the passage of the jsonobject to the API
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url, eSchool, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("message response string", String.valueOf((response)));
//                            if (response.)
                        // handle response
//                                passing the handled response to a callback
                        callback.done(response, null);
                    }
                }, error -> {
                    // TODO: Handle error
                    error.printStackTrace();
                    Log.d("EFU SCHOOL", error.toString());
                    callback.done(null, error);
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json");
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                // set retry policy to determine how long volley should wait before resending a failed request
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // add jsonObjectRequest to the queue
                requestQueue.add(jsonObjectRequest);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    callback.done(null, e);
//                }
            } else {
                Log.d("EFU MESSAGE", err.toString());
                callback.done(null, err);
            }
        });
    }


    public void queryMessageToTheApi() throws JSONException {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME1 + " WHERE "
                + UserContract.UserEntry.MESSAGE_SENT+ "=?", new String[]{"true"});
    Log.d(" Messages Entries", String.valueOf(cursor.getCount()));
    JSONArray sender = new JSONArray();
    // if Cursor is contains results
    if (cursor != null) {
        // move cursor to first row
        if (cursor.moveToFirst()) {
            do {
                // setting the value of the data in the database when looping
                JSONObject eMessage = new JSONObject();
                eMessage.put("message_id", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_ID)));
                eMessage.put("message_isdn", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_ISDN)));
                eMessage.put("message_content", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_CONTENT)));
                eMessage.put("student_card_code", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.STUDENT_CARD_CODE)));
                eMessage.put("message_sent", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.MESSAGE_SENT)));
                eMessage.put("card_id", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.STUDENT_CARD_ID)));
                eMessage.put("date", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.DATE)));

                sender.put(eMessage);
            } while (cursor.moveToNext());

            if(sender.length() > 0){
                this.persistSentMessagesOnTheServer(sender, (response, e) ->{
                    // performing a check to validate entries
                    // that have been persisted on the server
                    // and deleting such entries from the device's DB
                    if(e == null){
                        if(response.length() > 0){
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject entry = response.getJSONObject(i);
                                    if (!entry.isNull("message_id")) {
//                                        if(entry.getBoolean("saved")) {
                                            // persist data on the device's database (outgoing_notification)

                                            // delete entry from local DB
                                            dbHelper.deleteSmsData(entry.getString("message_id"));
//                                        }
                                    }

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }


        }
    }

    }


    public void queryStudentDetailsAndSendTOApi() throws JSONException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserContract.UserEntry.TABLE_NAME, null);//selecting all data entries from the database
        Log.d("Entries", String.valueOf(cursor.getCount()));
        JSONArray payloads = new JSONArray();
        // if Cursor is contains results
        if (cursor != null) {
            // move cursor to first row
            if (cursor.moveToFirst()) {
                do {
                    // setting the value of the data in the database when looping
                    JSONObject eSchoolLoad = new JSONObject();
                    eSchoolLoad.put("card_id", cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_ID)));
                    eSchoolLoad.put("card_number", Integer.toString(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER))));
                    eSchoolLoad.put("card_code", Integer.toString(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_CODE))));
                    eSchoolLoad.put("timestamp", cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_TIMESTAMP)));

                    payloads.put(eSchoolLoad);
                } while (cursor.moveToNext());

                if(payloads.length() > 0){
                    this.persistDataOnline(payloads, (response, e) ->{
                        // performing a check to validate entries
                        // that have been persisted on the server
                        // and deleting such entries from the device's DB
                        if(e == null){
                            if(response.length() > 0){
                                for (int i = 0; i < response.length(); i++){
                                    try {
                                        JSONObject entry = response.getJSONObject(i);
                                        if (!entry.isNull("response")) {
                                            if(entry.getString("response").equals("200")) {
                                                // persist data on the device's database (outgoing_notification)
                                                dbHelper = new UserReaderDbHelper(mContext);
                                                sqLiteDatabase = dbHelper.getReadableDatabase();
                                                UserReaderDbHelper.insertResponseFromApi(entry, sqLiteDatabase);
                                            }
                                        }
                                        // delete entry from local DB
                                        UserReaderDbHelper dbHelper = new UserReaderDbHelper(mContext);
                                        dbHelper.deleteData(entry.getJSONObject("payload").getString("card_id"));

                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    public void persistDataOnline(JSONArray payload, ArrayResponseCallback callback){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        using sharedpref to get the token of the school logged in
        Controller controller = new Controller(mContext, preferences);
        controller.getToken(mContext, (token, err) -> {//making use of the token gotten the sharedpref in the controller
            if (err == null) {
                String url = Globals.ESCHOOL_BASE_URL+"saveStudentLog";//this is were we set the base url and the endpoint
                Log.d(" First JSON ARRAY", payload.toString());
//                try {

//                    using volley api to get the various parameter that would aid the passage of the jsonobject to the API
                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                    JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url, payload, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            Log.d("Response string", String.valueOf((response)));
//                            if (response.)
                            // handle response
//                                passing the handled response to a callback
                                callback.done(response, null);
                        }
                    }, error -> {
                        // TODO: Handle error
                        error.printStackTrace();
                        Log.d("EFU PAYLOAD", error.toString());
                        callback.done(null, error);
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Accept", "application/json");
                            params.put("Content-Type", "application/json");
                            params.put("Authorization", "Bearer " + token);
                            return params;
                        }
                    };
                    // set retry policy to determine how long volley should wait before resending a failed request
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            30000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    // add jsonObjectRequest to the queue
                    requestQueue.add(jsonObjectRequest);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    callback.done(null, e);
//                }
            } else {
                Log.d("EFU PAYLOAD", err.toString());
                callback.done(null, err);
            }
        });
    }


    public void sendSms(JSONArray payload, JsonObjectResponse callback) throws JSONException {
                String url = "http://47.90.244.90:8081/sendsms";//this is were we set the base url and the endpoint
                JSONObject smsRequest = new JSONObject();
                smsRequest.put("smsRequest", payload);
                Log.d("JSON SMS ARRAY", smsRequest.toString());

                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, smsRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response string", String.valueOf((response)));
                        callback.done(response, null);
                    }
                }, error -> {
                    // TODO: Handle error
                    error.printStackTrace();
                    Log.d("EFU PAYLOAD", error.toString());
                    callback.done(null, error);
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json");
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };
                // set retry policy to determine how long volley should wait before resending a failed request
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // add jsonObjectRequest to the queue
                requestQueue.add(jsonObjectRequest);
    }

    public static void showOperationsDialog(String title, String description) {
        dialog = new LottieAlertDialog.Builder(mContext, DialogTypes.TYPE_LOADING)
                .setTitle(title)
                .setDescription(description)
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public static void showErrorMessage(String title, String description, @Nullable final SingleInputDialogCallback callback) {
        LottieAlertDialog errorCreationErrorDialog = new LottieAlertDialog
                .Builder(mContext, DialogTypes.TYPE_ERROR)
                .setTitle(title).setDescription(description)
                .setPositiveText("OK")
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog dialog) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.done("success");
                        }
                    }
                })
                .build();
        errorCreationErrorDialog.setCancelable(false);
        errorCreationErrorDialog.show();




    }

    public static void showSuccessMessage(String title, String description, @Nullable final SingleInputDialogCallback callback) {
        LottieAlertDialog dialog = new LottieAlertDialog
                .Builder(mContext, DialogTypes.TYPE_SUCCESS)
                .setTitle(title).setDescription(description)
                .setPositiveText("OK")
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog d) {
                        d.dismiss();
                        if (callback != null) {
                            callback.done("success");
                        }
                    }
                })
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }


    public void getToken(Context context, ResponseCallback callback){
        callback.done(appPref.getStringValue("token"), null);
    }

    public void getId(Context context, ResponseCallback callback){
        String id =  mPreferences.getString("id", null);
        callback.done(id, null);
    }



}
