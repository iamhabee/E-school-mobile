package com.efulltech.efupay.e_school;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;

import com.efulltech.efupay.e_school.db.UserContract;
import com.efulltech.efupay.e_school.db.UserReaderDbHelper;

import org.efulltech.efupay.e_school.R;
//import org.efulltech.orafucharles.e_school.R;


public class MyWorker extends Worker {
     private static UserReaderDbHelper dbHelper;


    @NonNull
    @Override
    public Result doWork() {
        Log.d("note", "work manager called");

        queryStudentDetailsAndSendTOApi();
        return Result.SUCCESS;
    }


    public void queryStudentDetailsAndSendTOApi(){
        dbHelper = new UserReaderDbHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserContract.UserEntry.TABLE_NAME, null);//selecting all data entries from the database
        Log.d("Entries", String.valueOf(cursor.getCount()));
        // if Cursor is contains results
        if (cursor != null) {
            // move cursor to first row
            if (cursor.moveToFirst()) {
                do {
                    // setting the value of the data in the database when looping
                    ESchoolLoad eSchoolLoad = new ESchoolLoad(this.getApplicationContext());
                    eSchoolLoad.setCardNumber(Integer.toString(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER))));
                    eSchoolLoad.setCardCode(Integer.toString(cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_CODE))));
                    eSchoolLoad.setTimeStamp(cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.CLOCK_TIMESTAMP)));

//                    initiating the persisDataOnline function that has 2 parameters after looping the values that is sent it also communicate with the API
                    eSchoolLoad.persistDataOnline((res, err) ->{
                        if(err == null){
                            // success
                            Log.d("PERSISTENCE ", "SENT "+UserContract.UserEntry._ID+" RESPONSE:: "+res);
                        }else{
                            Log.d("WORKER", err.toString());
                        }
                    });

                } while (cursor.moveToNext());
            }
        }
    }



    private void displayNotification(String task, String desc){

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("simplifiedcoding" , "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

//        this method displays the notification
        manager.notify(1, builder.build());
    }



}
