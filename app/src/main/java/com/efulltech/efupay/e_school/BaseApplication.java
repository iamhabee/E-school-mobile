package com.efulltech.efupay.e_school;

import android.app.Application;
import android.widget.Toast;

import com.efulltech.efupay.e_school.utils.Controller;

import java.util.Timer;
import java.util.TimerTask;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Controller controller = new Controller(this);
        Toast.makeText(this, "Work man",Toast.LENGTH_SHORT).show();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                controller.queryStudentDetailsAndSendTOApi();
            }
        }, 0, 10000);
    }
}
