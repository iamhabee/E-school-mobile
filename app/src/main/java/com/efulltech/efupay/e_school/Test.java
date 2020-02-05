package com.efulltech.efupay.e_school;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.efulltech.efupay.e_school.db.UserContract;
import com.efulltech.efupay.e_school.db.UserReaderDbHelper;

import org.efulltech.efupay.e_school.R;
//import org.efulltech.orafucharles.e_school.R;

import org.json.JSONObject;

public class Test extends AppCompatActivity {


    UserReaderDbHelper db;
    SQLiteDatabase sqLiteDatabase;
    public EditText mCard;
    public Button mButton;
    public TextView mTextView;
    public TextView mTextView1;
    public TextView mTextView2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        db = new UserReaderDbHelper(this);

        mCard = findViewById(R.id.cardNumber);
        mButton = findViewById(R.id.button2);
        mTextView = findViewById(R.id.textView2);
        mTextView1 = findViewById(R.id.textView3);
        mTextView2 = findViewById(R.id.textView4);


        String URL = "";
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         Log.d("rest response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("rest response", error.toString());
                    }
                }
        );
        requestQueue.add(objectRequest);


        readContacts();
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Validate();
//            }
//        });

    }

//
//    public void  Validate() {
//        String cardNum = mCard.getText().toString().trim();
//        db = new UserReaderDbHelper(this);
//        sqLiteDatabase = db.getReadableDatabase();
//        db.addStudentDetails(cardNum, sqLiteDatabase);
//        Toast.makeText(Test.this, "Details sent", Toast.LENGTH_SHORT).show();
//        Toast.makeText(Test.this, cardNum, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Test.this, cardNum, Toast.LENGTH_SHORT).show();
//        Log.d("the number", cardNum);
//    }


    public void readContacts(){
        String id;
        String cardSNumd = "";
        String cardCode = "";
        String timeStamp  = "";
        UserReaderDbHelper userReaderDbHelper = new UserReaderDbHelper(this);
        SQLiteDatabase db = userReaderDbHelper.getReadableDatabase();
        Cursor c = userReaderDbHelper.readDatabase(db);
        String info = "";
        while (c.moveToNext()){
            id = Integer.toString(c.getInt(c.getColumnIndex(UserContract.UserEntry.CLOCK_ID)));
            cardSNumd = Integer.toString(c.getInt(c.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_SERIAL_NUMBER)));
            cardCode = Integer.toString(c.getInt(c.getColumnIndex(UserContract.UserEntry.CLOCK_CARD_CODE)));
             timeStamp = c.getString(c.getColumnIndex(UserContract.UserEntry.CLOCK_TIMESTAMP));

            info = info+"\n\n"+ "id : "+id+"\nClckid : "+cardSNumd+"\n"+cardCode+" \ntimestamp : "+timeStamp;
        }
        Log.d("seeit", info);

        mTextView.setText(info);
    }







}

