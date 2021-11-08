package com.buzzware.nowapp.Screens.UserScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buzzware.nowapp.EventMessages.OnRestaurantSelectedMessage;
import com.buzzware.nowapp.EventMessages.OpenSearchFragment;
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard.BuisnessHomeFragment;
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard.SearchFragment;
import com.buzzware.nowapp.Fragments.UserFragments.HomeFragment;
import com.buzzware.nowapp.Fragments.UserFragments.SettingsFragment;
import com.buzzware.nowapp.Libraries.libactivities.VideoCameraActivity;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityHomeBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener {
    public static ActivityHomeBinding mBinding;
    Context context;
    Fragment selectedFragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        try {
            Init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Init() {
        context = this;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("home").commit();
        //clicks
        mBinding.settings.setOnClickListener(this);
        mBinding.home.setOnClickListener(this);
        mBinding.openCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == mBinding.settings) {

            selectedFragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("home").commit();
            SetBottomNavigation(1);

        } else if (v == mBinding.home) {

            selectedFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("settings").commit();
            SetBottomNavigation(0);

        } else if (v == mBinding.openCamera) {

            checkCameraPermissions();

        }
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() >= 0)
        {
           super.onBackPressed();
        }
        else
        {
            finish();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pushSearchFragment(OpenSearchFragment event) {

        Fragment s = new SearchFragment(event.isFromFilter, event.filtersList);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, s).addToBackStack("Search").commit();

    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pushSearchFragment(OnRestaurantSelectedMessage event) {

        Fragment s = new BuisnessHomeFragment(event.id);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, s).addToBackStack("BusinessHome").commit();

    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

    }
    private void checkCameraPermissions() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    startActivity(new Intent(Home.this, VideoCameraActivity.class));

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();

            }
        }).check();
    }

    public static void SetBottomNavigation(int position) {
        if (position == 0) {
            mBinding.homeIcon.setImageResource(R.drawable.ic_home_pink);
            mBinding.settingsIcon.setImageResource(R.drawable.ic_settings);
            mBinding.homeDot.setVisibility(View.VISIBLE);
            mBinding.settingDot.setVisibility(View.GONE);
        } else if (position == 1) {
            mBinding.homeIcon.setImageResource(R.drawable.ic_home);
            mBinding.settingsIcon.setImageResource(R.drawable.ic_settings_pink);
            mBinding.homeDot.setVisibility(View.GONE);
            mBinding.settingDot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}