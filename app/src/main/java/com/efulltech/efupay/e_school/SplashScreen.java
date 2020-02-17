package com.efulltech.efupay.e_school;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.efulltech.efupay.e_school.utils.AppPref;
import com.efulltech.efupay.e_school.utils.Controller;
import org.efulltech.efupay.e_school.R;
import org.json.JSONException;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

            Controller controller = new Controller(this);
        Thread myThread= new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                    controller.checkIfUserHasLoggedInBefore();
                }catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
