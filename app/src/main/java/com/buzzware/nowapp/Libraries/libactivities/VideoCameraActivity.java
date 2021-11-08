package com.buzzware.nowapp.Libraries.libactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.nowapp.Libraries.base.BaseActivity;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.BaseCameraActivity;
import com.camerakit.CameraKitView;

import java.io.File;

import butterknife.BindView;

/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor
 * @Date 2018/8/22 10:54
 * @description 视频拍摄界面
 */
public class VideoCameraActivity extends BaseCameraActivity {

    private CameraKitView cameraKitView;
    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, VideoCameraActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_screen);
        onCreateActivity();
        videoWidth = 720;
        videoHeight = 1280;
        cameraWidth = 1280;
        cameraHeight = 720;
    }
}
