package org.example.orafucharles.e_school.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.example.orafucharles.e_school.db.UserContract.UserEntry.TABLE_NAME;
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
                UserContract.UserEntry.CLOCK_CARD_NUMBER+ " TEXT NOT NULL, "+
                UserContract.UserEntry.CLOCK_TIMESTAMP + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public static void addStudentDetails(String cardnumber, SQLiteDatabase database){

        //getting the current date and time when message was sent to the local DB
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm a");
        newDate = sdf.format(new Date());

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.UserEntry.CLOCK_CARD_NUMBER, cardnumber);
        contentValues.put(UserContract.UserEntry.CLOCK_TIMESTAMP ,newDate);

        Log.d("timestampp", newDate);
        Log.d("card number", cardnumber);
        database.insert(TABLE_NAME, null, contentValues);
        Log.d("Database operations", "Item added");
    }


    public static Cursor readDatabase(SQLiteDatabase database){
        String[] projections = {UserContract.UserEntry.CLOCK_ID, UserContract.UserEntry.CLOCK_CARD_NUMBER, UserContract.UserEntry.CLOCK_TIMESTAMP};
        Cursor cursor = database.query(TABLE_NAME,
                projections, null, null,null, null, null);
        return  cursor;
    }


    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
       return db.delete(TABLE_NAME, "clock_id = ?", new String[] {id});
    }

}
