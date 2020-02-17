package com.efulltech.efupay.e_school.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import static com.efulltech.efupay.e_school.db.UserContract.UserEntry.TABLE_NAME;
import static com.efulltech.efupay.e_school.db.UserContract.UserEntry.TABLE_NAME1;
import static com.efulltech.efupay.e_school.db.UserContract.UserEntry.TABLE_NAME2;

public class UserReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "clock.db";
    public static String newDate;
    public String timing;


    public UserReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table "+ TABLE_NAME +
                " ( " + UserContract.UserEntry.CLOCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER+ " TEXT NOT NULL, "+
                UserContract.UserEntry.CLOCK_CARD_CODE+ " TEXT NOT NULL, "+
                UserContract.UserEntry.CLOCK_TIMESTAMP + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLE2 = "create table "+ TABLE_NAME1 +
                " ( " + UserContract.UserEntry.ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserContract.UserEntry.MESSAGE_ID+ " TEXT NOT NULL, "+
                UserContract.UserEntry.MESSAGE_ISDN+ " TEXT NOT NULL, "+
                UserContract.UserEntry.MESSAGE_CONTENT+ " TEXT NOT NULL, "+
                UserContract.UserEntry.MESSAGE_DELIVERED+ " TEXT NOT NULL, "+
                UserContract.UserEntry.MESSAGE_SENT+ " TEXT NOT NULL, "+
                UserContract.UserEntry.STUDENT_CARD_ID+ " TEXT NOT NULL, "+
                UserContract.UserEntry.DATE+ " TEXT NOT NULL, "+
                UserContract.UserEntry.STUDENT_CARD_CODE + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE2);

        String CREATE_TABLE3 = "create table "+ TABLE_NAME2 +
                " ( " + UserContract.UserEntry.D_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserContract.UserEntry.FIRST_NAME+ " TEXT NOT NULL, "+
                UserContract.UserEntry.LAST_NAME+ " TEXT NOT NULL, "+
                UserContract.UserEntry.PHOTO_URL+ " TEXT NOT NULL, "+
                UserContract.UserEntry.CLASS + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE3);


    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

//    public  static void insertResponseFromApi(String msgId, String msgIsdn, String msgContent, Boolean msgDelivered, Boolean msgSent, String msgCardCode , SQLiteDatabase database2){
    public  static void insertResponseFromApi(JSONObject entry, SQLiteDatabase database2) throws JSONException {
        if(entry.getString("response").equals("200")) {
            Log.d("response code available", entry.toString());

            // compose sms body
            String phoneNo = entry.getJSONObject("message").getString("receiver_phone");
            String appendage = "has just clocked in at";
            if (!entry.getJSONObject("log").getBoolean("is_logged_in")) {
                appendage = "has just clocked out as at";
            }
            String title = entry.getString("studentSchoolName") + "(" + entry.getJSONObject("message").getString("title") + ")";
            String body = "Hello " + entry.getJSONObject("message").getString("receiver_name") +
                    ", your child " + entry.getString("studentName") + " "
                    + appendage + " " + entry.getJSONObject("payload").getString("timestamp");

            ContentValues contentValues = new ContentValues();
            contentValues.put(UserContract.UserEntry.MESSAGE_ID, entry.getJSONObject("message").getString("message_id"));
            contentValues.put(UserContract.UserEntry.MESSAGE_ISDN, entry.getJSONObject("message").getString("receiver_phone"));
            contentValues.put(UserContract.UserEntry.MESSAGE_CONTENT, title+body);
            contentValues.put(UserContract.UserEntry.MESSAGE_DELIVERED, "false");
            contentValues.put(UserContract.UserEntry.MESSAGE_SENT, "false");
            contentValues.put(UserContract.UserEntry.STUDENT_CARD_CODE, entry.getJSONObject("payload").getString("card_code"));
            contentValues.put(UserContract.UserEntry.STUDENT_CARD_ID, entry.getJSONObject("payload").getString("card_id"));
            contentValues.put(UserContract.UserEntry.DATE, entry.getJSONObject("payload").getString("timestamp"));

            Log.d("DB NEW ENTRY", entry.toString());
            database2.insert(TABLE_NAME1, null, contentValues);
            Log.d("notification", "Item added to database");
        }
    }
    public static void addStudentDetails(String cardSerialNumber, String cardCode, SQLiteDatabase database){

        //getting the current date and time when message was sent to the local DB
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm a");
        newDate = sdf.format(new Date());

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER, cardSerialNumber);
        contentValues.put(UserContract.UserEntry.CLOCK_CARD_CODE, cardCode);
        contentValues.put(UserContract.UserEntry.CLOCK_TIMESTAMP ,newDate);

        Log.d("timestampp", newDate);
        Log.d("card serial number", cardSerialNumber);
        Log.d("card code", cardCode);
        database.insert(TABLE_NAME, null, contentValues);
        Log.d("Database operations", "Item added to database");
    }

    public static void liveStudenentDetails(String firstname, String lastname, String dClass, String photoUrl, SQLiteDatabase database){

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.UserEntry.FIRST_NAME, firstname);
        contentValues.put(UserContract.UserEntry.LAST_NAME, lastname);
        contentValues.put(UserContract.UserEntry.CLASS ,dClass);
        contentValues.put(UserContract.UserEntry.PHOTO_URL ,photoUrl);

        Log.d("firstname", firstname);
        Log.d("lastname", lastname);
        Log.d("class", dClass);
        database.insert(TABLE_NAME2, null, contentValues);
        Log.d("Database operations", "students added to database");
    }


    public static Cursor readDatabase(SQLiteDatabase database){
        String[] projections = {UserContract.UserEntry.CLOCK_ID, UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER, UserContract.UserEntry.CLOCK_CARD_CODE, UserContract.UserEntry.CLOCK_TIMESTAMP};
        Cursor cursor = database.query(TABLE_NAME,
                projections, null, null,null, null, null);
        return  cursor;
    }


    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("check id", id);
       return db.delete(TABLE_NAME, "clock_id = ?", new String[] {id});
    }

    public Integer deleteSmsData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
       return db.delete(TABLE_NAME1, UserContract.UserEntry.MESSAGE_ID+" = ?", new String[] {id});
    }

    public void updateSmsData(String id) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(UserContract.UserEntry.MESSAGE_SENT,"true");

            db.update(TABLE_NAME1, contentValues, UserContract.UserEntry.MESSAGE_ID+" = ?" ,new String[] {id});
    }
}
