package com.efulltech.efupay.e_school;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;

import android.os.Bundle;

//import org.efulltech.orafucharles.e_school.R;

import org.efulltech.efupay.e_school.R;

import java.util.concurrent.TimeUnit;

public class WorkManager extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manager);


                    PeriodicWorkRequest periodicWorkRequest =
                            new PeriodicWorkRequest.Builder(MyWorker.class, 5, TimeUnit.SECONDS)
                                    .addTag("periodic_work")
                                    .build();

                    assert androidx.work.WorkManager.getInstance() != null;
                    androidx.work.WorkManager.getInstance().enqueue(periodicWorkRequest);

        /* trigger work manager every 10sec */
    }







    }

    

