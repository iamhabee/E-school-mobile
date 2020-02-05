package com.efulltech.efupay.e_school;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.efull.cardkey.CardKeyAB;

import org.efulltech.efupay.e_school.R;
//import org.efulltech.orafucharles.e_school.R;

public class NFC extends AppCompatActivity {

    private EditText etCardSN;
    private EditText etCardCode;
    private Button btnConfirm;
    //
    private String strCardSN;
    private String strCardCode;
    //
    private Dialog mDialog;
    //
    private NfcAdapter nfcAdapter;
    private boolean isnews = true;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        etCardSN=(EditText)findViewById(R.id.ActivityMain_InputCardSN);
        etCardCode=(EditText)findViewById(R.id.ActivityMain_InputCardCode);
        btnConfirm=(Button)findViewById(R.id.ActivityMain_Confirm);

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
                new String[] { NfcA.class.getName() } };// 允许扫描的标签类型

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

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        // 得到是否检测到ACTION_TECH_DISCOVERED触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            //
//            LoadingDialog.CloseDialog(mDialog);
            // 处理该intent
            int iReturn;
            iReturn=processIntent(intent);
            if(iReturn==0x00){
                etCardSN.setText(strCardSN);
                etCardCode.setText(strCardCode);
            }else{
                ToastShow("Read Efull Card Failure...");
            }
        }
    }
    //读取NFC信息
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

            byte[] KeyAB=CardKeyAB.GetKeyAB(bCardSN);
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

    private void ToastShow(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
