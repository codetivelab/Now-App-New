package com.buzzware.nowapp.Screens.BuisnessScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;

public class BuisnessApplicationStartUpScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buisness_application_start_up_screen);
    }

    public void StartApplication(View view) {
        Intent intent= new Intent(BuisnessApplicationStartUpScreen.this, BuisnessApplication.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}