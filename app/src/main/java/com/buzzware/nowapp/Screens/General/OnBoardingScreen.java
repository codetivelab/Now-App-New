package com.buzzware.nowapp.Screens.General;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments.FirstScreen;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityOnBoardingScreenBinding;

public class OnBoardingScreen extends AppCompatActivity {

    ActivityOnBoardingScreenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_on_boarding_screen);
        StartFirstScreen();
    }

    private void StartFirstScreen() {
        getSupportFragmentManager().beginTransaction().replace(R.id.onBoardingContainer, new FirstScreen()).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}