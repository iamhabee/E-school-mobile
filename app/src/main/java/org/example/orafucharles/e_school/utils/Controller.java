package org.example.orafucharles.e_school.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.example.orafucharles.e_school.api.ResponseCallback;

import static android.content.Context.MODE_PRIVATE;

public class Controller {

    Context mContext;
    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    public Controller(Context context, SharedPreferences preferences) {
        mContext = context;
        mPreferences = preferences;

    }

    public void getToken(Context context, ResponseCallback callback){
        String token =  mPreferences.getString("token", null);
        callback.done(token, null);
    }
}
