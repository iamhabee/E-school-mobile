package org.example.orafucharles.e_school;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Dialog closeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

}