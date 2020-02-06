package com.efulltech.efupay.e_school;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.efulltech.efupay.e_school.api.ResponseCallback;

import com.efulltech.efupay.e_school.utils.Controller;
import com.efulltech.efupay.e_school.utils.Globals;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ESchoolLoad {
    private String cardNumber;
    private String cardCode;
    private int cardId;
        private String timeStamp;
        private boolean isSent;
        private String  responseCode;
        private Context mContext;



    public ESchoolLoad(Context context){
        super();
         mContext = context;
    }


    public ESchoolLoad(String cardCode, String responseCode, String cardNumber, String timeStamp, boolean isSent) {
        this.cardNumber = cardNumber;
        this.timeStamp = timeStamp;
        this.isSent = isSent;
        this.responseCode = responseCode;
        this.cardCode = cardCode;
        this.cardId = cardId;


    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSent() {
        return isSent;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }






    public void persistDataOnline(ResponseCallback callback){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        using sharedpref to get the token of the school logged in
        Controller controller = new Controller(mContext, preferences);
        controller.getToken(mContext, (token, err) -> {//making use of the token gotten the sharedpref in the controller
            if (err == null) {
                String url = Globals.ESCHOOL_BASE_URL+"saveStudentLog";//this is were we set the base url and the endpoint
                JSONObject payload = new JSONObject();
                try {//passing the various json object that will be sent to the API
                    payload.put("card_serial_number", this.cardNumber);
                    payload.put("card_code", this.cardCode);
                    payload.put("log_timestamp", this.timeStamp);
                    Log.d("EFU PAYLOAD OBJECT:", payload.toString());

//                    using volley api to get the various parameter that would aid the passage of the jsonobject to the API
                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("Response string", String.valueOf((response)));
//                            if (response.)
                            // handle response
                            try {
//                                passing the handled response to a callback
                                callback.done(response.getString("response"), null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.done(null, e);
                }
            } else {
                Log.d("EFU PAYLOAD", err.toString());
                callback.done(null, err);
            }
        });
      }



}



