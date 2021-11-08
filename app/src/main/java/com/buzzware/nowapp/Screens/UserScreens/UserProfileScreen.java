package com.buzzware.nowapp.Screens.UserScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;

import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBuinessPassword;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityUserProfileScreenBinding;

public class UserProfileScreen extends AppCompatActivity {

    ActivityUserProfileScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileScreenBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {

        binding.changePasswordRL.setOnClickListener(v -> {
            startActivity(new Intent(UserProfileScreen.this, ChangeBuinessPassword.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}



