package com.buzzware.nowapp.Screens.BuisnessScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.FirstTabFragment;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.ActivityBuisnessApplicationBinding;

public class BuisnessApplication extends AppCompatActivity {

    ActivityBuisnessApplicationBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_buisness_application);

        SetFirstTabFragment();
    }

    private void SetFirstTabFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new FirstTabFragment()).addToBackStack("firstTab").commit();
    }



    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() >= 0)
        {
            super.onBackPressed();
        }
        else
        {
            finish();
        }

    }
}