package org.example.orafucharles.e_school;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import org.example.orafucharles.e_school.MyWorker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

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

    

