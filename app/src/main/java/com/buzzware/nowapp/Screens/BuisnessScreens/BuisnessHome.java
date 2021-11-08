package com.buzzware.nowapp.Screens.BuisnessScreens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard.BuisnessHomeFragment;
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard.BuisnessSettingsFragment;
import com.buzzware.nowapp.Libraries.libactivities.VideoCameraActivity;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityBuisnessHomeBinding;
import com.buzzware.nowapp.services.UploadVideoService;
import com.buzzware.nowapp.utils.FileUtilsClass;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class BuisnessHome extends AppCompatActivity implements View.OnClickListener {

    public static ActivityBuisnessHomeBinding mBinding;
    Fragment selectedFragment = new BuisnessHomeFragment();

    private static final int REQUEST_VIDEO_CAPTURE = 1010;
    String selectedVideoPath = "";

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_buisness_home);

        SetFirstScreen();
        initFirebase();

        //clicks
        mBinding.settings.setOnClickListener(this);
        mBinding.home.setOnClickListener(this);
        mBinding.openCamera.setOnClickListener(this);
    }


    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void SetFirstScreen() {
        getSupportFragmentManager().beginTransaction().replace(R.id.buisnessContainer, selectedFragment).addToBackStack("buisnessHome").commit();
    }
    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0)
        {
            super.onBackPressed();
        }
        else
        {
            finish();
        }

    }
    @Override
    public void onClick(View v) {

        if (v == mBinding.settings) {

            selectedFragment = new BuisnessSettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.buisnessContainer, selectedFragment).addToBackStack("home").commit();
            SetBottomNavigation(1);

        } else if (v == mBinding.home) {

            selectedFragment = new BuisnessHomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.buisnessContainer, selectedFragment).addToBackStack("settings").commit();
            SetBottomNavigation(0);

        } else if (v == mBinding.openCamera) {

            if (Constant.GetConstant().getNetworkInfo(BuisnessHome.this)) {
                GetPermission();
            } else {
                UIUpdate.GetUIUpdate(BuisnessHome.this).ShowToastMessage(getString(R.string.no_internet));
            }
        }
    }

    private void GetPermission() {
        Dexter.withActivity(BuisnessHome.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    checkIfExceededQuota();
                } else {
                    UIUpdate.GetUIUpdate(BuisnessHome.this).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void checkIfExceededQuota() {

        getMyPosts();
    }

    private void getMyPosts() {

        db.collection("UserPosts")
                .get()
                .addOnCompleteListener(task -> {

                    parseMyPostsSnapshot(task);
                });
    }

    private void parseMyPostsSnapshot(Task<QuerySnapshot> task) {

        int noOfPostsCreatedToday = 0;

        if (task.isSuccessful()) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                Log.d("TAG14123", document.getId() + " => " + document.getData());

                PostsModel postsModel = document.toObject(PostsModel.class);

                postsModel.postId = document.getId();

                if (postsModel.belongsToCurrentUser(this, null) && postsModel.createdToday())
                {
                    noOfPostsCreatedToday ++ ;
                }

            }

            if (noOfPostsCreatedToday >= 2)
            {
                showErrorAlert(getResources().getString(R.string.exceeded_quota));

            }
            else
            {
                startActivity(new Intent(this, VideoCameraActivity.class));
            }

        } else {

            UIUpdate.GetUIUpdate(this).AlertDialog("Alert", task.getException().getLocalizedMessage());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

    private void showErrorAlert(String error) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Alert");
        builder1.setMessage(error);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            selectedVideoPath = FileUtilsClass.getPath(BuisnessHome.this, intent.getData());
            GetVideoThumbnail(intent.getData());
        }

    }

    private void GetVideoThumbnail(Uri data) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = BuisnessHome.this.getContentResolver().query(data, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedThumbnail = Base64.encodeToString(byteArray, Base64.DEFAULT);
        startUploadService(encodedThumbnail);

    }

    public void startUploadService(String thumbnail) {
        Intent serviceIntent = new Intent(BuisnessHome.this, UploadVideoService.class);
        // serviceIntent.putExtra(Constant.GetConstant().getUserLocation(), UserSessions.GetUserSession().getUserLocation(getContext()));
        // serviceIntent.putExtra(Constant.GetConstant().getUserLatitude(), UserSessions.GetUserSession().getUserLatitude(getContext()));
        // serviceIntent.putExtra(Constant.GetConstant().getUserLongitude(), UserSessions.GetUserSession().getUserLongitude(getContext()));
        serviceIntent.putExtra(getString(R.string.path), selectedVideoPath);
        serviceIntent.putExtra(Constant.GetConstant().getThumbnail(), thumbnail);
        //  Log.e("knas", UserSessions.GetUserSession().getUserLocation(getContext())+ " : "+ UserSessions.GetUserSession().getUserLatitude(getContext()) +" : "+UserSessions.GetUserSession().getUserLongitude(getContext()));
        ContextCompat.startForegroundService(BuisnessHome.this, serviceIntent);
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
}