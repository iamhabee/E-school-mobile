package com.efulltech.efupay.e_school;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.efull.cardkey.CardKeyAB;
import com.efull.smsgateway.api.http.HttpHelper;
import com.efull.smsgateway.api.json.JsonSMS;
import com.efull.smsgateway.api.json.RequestJSON;
import com.efulltech.efupay.e_school.*;
import com.efulltech.efupay.e_school.db.UserContract;
import com.efulltech.efupay.e_school.db.UserReaderDbHelper;
import com.efulltech.efupay.e_school.utils.Controller;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.efulltech.efupay.e_school.R;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.efulltech.efupay.e_school.db.UserContract.UserEntry.TABLE_NAME2;

public class MainActivity extends AppCompatActivity {
    private TextView textViewResult;
    private NfcAdapter nfcAdapter;
    UserReaderDbHelper db;
    private boolean isnews = true;
    SQLiteDatabase sqLiteDatabase;
    private Dialog closeDialog;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String strCardSN;
    UserReaderDbHelper dbHelper;
    private String strCardCode;
    private EditText etCardSN;
    private EditText etCardCode;
    private Controller controller;
    public SharedPreferences preferences;
    static final int TIME_OUT = 5000;

    static final int MSG_DISMISS_DIALOG = 0;

    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new UserReaderDbHelper(this);
        dbHelper = new UserReaderDbHelper(this);
        controller = new Controller(this,preferences);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addCategory("*/*");
        mFilters = new IntentFilter[] { ndef };// 过滤器
        mTechLists = new String[][] {
                new String[] { MifareClassic.class.getName() },
                new String[] { NfcA.class.getName() } };

        //this is were we use volley to return a response from our api
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
    }





    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        // 得到是否检测到ACTION_TECH_DISCOVERED触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            int iReturn;
            iReturn=processIntent(intent);
            if(iReturn==0x00){
                Validate();
            }else{
                LottieAlertDialog errorCreationErrorDialog = new LottieAlertDialog
                        .Builder(this, DialogTypes.TYPE_ERROR)
                        .setTitle("Authentication Error").setDescription("Please try again")
                        .setPositiveText("OK")
                        .setPositiveListener(new ClickListener() {
                            @Override
                            public void onClick(LottieAlertDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .build();
                errorCreationErrorDialog.setCancelable(false);
                errorCreationErrorDialog.show();
                //        setting a timer
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        errorCreationErrorDialog.dismiss();
                    }
                }, 2500, 1000);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // 得到是否检测到ACTION_TECH_DISCOVERED触发
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters,
                mTechLists);
        if (isnews) {
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent()
                    .getAction())) {
                // 处理该intent
                processIntent(getIntent());
                isnews = false;
            }
        }
    }

    public void  Validate() {
        String cardSerialNumber= strCardSN;
        String cardCode= strCardCode;
        db = new UserReaderDbHelper(this);
        sqLiteDatabase = db.getReadableDatabase();
        db.addStudentDetails(cardSerialNumber,cardCode, sqLiteDatabase);

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME2 + " WHERE "
                    + UserContract.UserEntry.CARD_CODE+ "=?", new String[]{cardCode});

//                getting various parameter of the student on tap of the card
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    String first_name  = cursor.getString(cursor.getColumnIndex("first_name"));
                    Log.e("dfname", "fname = " + first_name);
                    Toast.makeText(this, first_name, Toast.LENGTH_SHORT).show();

                    String last_name  = cursor.getString(cursor.getColumnIndex("last_name"));
                    Log.e("dlname", "lname = " + last_name);
                    Toast.makeText(this, first_name, Toast.LENGTH_SHORT).show();

                    String dClass  = cursor.getString(cursor.getColumnIndex("class"));
                    Log.e("dclass", "class = " + first_name);
                    Toast.makeText(this, dClass, Toast.LENGTH_SHORT).show();



                    //this is a success alert dialog
                    LottieAlertDialog dialog = new LottieAlertDialog
                            .Builder(this, DialogTypes.TYPE_SUCCESS)
                            .setTitle(first_name +" "+ last_name).setDescription(dClass)
                            .setPositiveText("OK")
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog d) {
                                    d.dismiss();
                                }
                            })
                            .build();
                    dialog.setCancelable(false);
                    dialog.show();
//        setting a timer
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2500, 1000);



                }while(cursor.moveToNext());
            }
        }



    }

    private int processIntent(Intent intent){
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareClassic") >= 0) {
                haveMifareUltralight = true;
                break;
            }
        }
        if (!haveMifareUltralight) {
            return 0x01;
        }

        try{

            MifareClassic mfc = MifareClassic.get(tag);

            mfc.connect();
            //读卡号
            byte[] bCardSN=intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

            byte[] KeyAB= CardKeyAB.GetKeyAB(bCardSN);
            //密钥认证
            //byte[] KeyAB={(byte)0x4d,(byte)0x24,(byte)0x7b,(byte)0x5a,(byte)0x68,(byte)0xe9,(byte)0x4d,(byte)0x24,(byte)0x7b,(byte)0x5a,(byte)0x68,(byte)0xe9};
            boolean auth=mfc.authenticateSectorWithKeyB(1, KeyAB);
            if(auth==false){
                return 0x02;
            }
            //读数据
            byte[] bBuffer=mfc.readBlock(4);
            //卡内编号
            int iLen=0;
            for(int i=0;i<16;i++){
                if(bBuffer[i]!=0x00){
                    iLen++;
                }
            }
            byte[] bCardCode=new byte[iLen];
            for(int i=0;i<iLen;i++){
                bCardCode[i]=bBuffer[i];
            }
            strCardCode=new String(bCardCode);
            //卡序号
            long lngCardSN;
            lngCardSN=(long)(bCardSN[0]&0xff);
            lngCardSN+=(long)(bCardSN[1]&0xff)*256;
            lngCardSN+=(long)(bCardSN[2]&0xff)*256*256;
            lngCardSN+=(long)(bCardSN[3]&0xff)*256*256*256;
            strCardSN=String.valueOf(lngCardSN);
        }catch(Exception e){
            e.printStackTrace();
            return 0xff;
        }

        return 0;
    }

    @Override
    public void onBackPressed() {

        closeDialog = new Dialog(this);
        closeDialog.setContentView(R.layout.close_app_dialog);
        closeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        closeDialog.show();

        Button yes = closeDialog.findViewById(R.id.yes);
        Button no = closeDialog.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog.dismiss();
               finish();
                Intent loginIntent = new Intent(MainActivity.this, Login.class );
                startActivity(loginIntent);

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog.dismiss();
            }
        });
        return;

    }


    private void ToastShow(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }




}
