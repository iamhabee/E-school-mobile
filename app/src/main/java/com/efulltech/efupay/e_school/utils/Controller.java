package com.efulltech.efupay.e_school.utils;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.efulltech.efupay.e_school.ESchoolLoad;
import com.efulltech.efupay.e_school.api.ArrayResponseCallback;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    private  static final int TIME_OUT = 5000;

    private  static final int MSG_DISMISS_DIALOG = 0;
    private AlertDialog mAlertDialog;

    private static final String TAG = "Controller";
    private static LottieAlertDialog dialog;
   private static Context mContext;
    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;
    private ProgressDialog progressDialog;
    private LottieAlertDialog studentSuccessDialog;
    private static UserReaderDbHelper dbHelper;
    private Gson gson = new Gson();

    public Controller(Context context, SharedPreferences preferences) {

        this.mContext = context;
        this.mPreferences = preferences;
        dbHelper = new UserReaderDbHelper(mContext);
    }

    public Controller(Context context) {
        this.mContext = context;
        dbHelper = new UserReaderDbHelper(mContext);
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
                                        if(!entry.isNull("response")){
                                            // get the card id
                                            String card_id = entry.getJSONObject("payload").getString("card_id");
                                            // delete entry from local DB
                                            UserReaderDbHelper dbHelper = new UserReaderDbHelper(mContext);
                                            dbHelper.deleteData(card_id);
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

    public void persistDataOnline(JSONArray payload, ArrayResponseCallback callback){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        using sharedpref to get the token of the school logged in
        Controller controller = new Controller(mContext, preferences);
        controller.getToken(mContext, (token, err) -> {//making use of the token gotten the sharedpref in the controller
            if (err == null) {
                String url = Globals.ESCHOOL_BASE_URL+"saveStudentLog";//this is were we set the base url and the endpoint
                Log.d("JSON ARRAY", payload.toString());
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
        String token =  mPreferences.getString("token", null);
        callback.done(token, null);
    }





}
