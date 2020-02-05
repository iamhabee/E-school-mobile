package com.efulltech.efupay.e_school.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.efulltech.efupay.e_school.ESchoolLoad;
import com.efulltech.efupay.e_school.api.ResponseCallback;
import com.efulltech.efupay.e_school.db.UserContract;
import com.efulltech.efupay.e_school.db.UserReaderDbHelper;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

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

    public Controller(Context context, SharedPreferences preferences) {

        this.mContext = context;
        this.mPreferences = preferences;
        dbHelper = new UserReaderDbHelper(mContext);
    }

    public Controller(Context context) {
        this.mContext = context;
        dbHelper = new UserReaderDbHelper(mContext);
    }


    public void queryStudentDetailsAndSendTOApi(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserContract.UserEntry.TABLE_NAME, null);//selecting all data entries from the database
        Log.d("Entries", String.valueOf(cursor.getCount()));
        // if Cursor is contains results
        if (cursor != null) {
            // move cursor to first row
            if (cursor.moveToFirst()) {
                do {
                    // setting the value of the data in the database when looping
                    ESchoolLoad eSchoolLoad = new ESchoolLoad(mContext);
                    eSchoolLoad.setCardId(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_ID)));
                    eSchoolLoad.setCardNumber(Integer.toString(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER))));
                    eSchoolLoad.setCardCode(Integer.toString(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_CODE))));
                    eSchoolLoad.setTimeStamp(cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_TIMESTAMP)));

//                    initiating the persisDataOnline function that has 2 parameters after looping the values that is sent it also communicate with the API
                    eSchoolLoad.persistDataOnline((res, err) ->{
                        if(err == null){
                            // success
                            Log.d("PERSISTENCE ", "SENT "+UserContract.UserEntry._ID+" RESPONSE:: "+res);
                            UserReaderDbHelper userReaderDbHelper = new UserReaderDbHelper(mContext);
                            userReaderDbHelper.deleteData(Integer.toString(eSchoolLoad.getCardId()));
                        }else{
                            Log.d("WORKER", err.toString());
                        }
                    });

                } while (cursor.moveToNext());
            }
        }
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
