package com.buzzware.nowapp.Screens;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import butterknife.BindView;

import com.buzzware.nowapp.Libraries.libactivities.TrimVideoActivity;
import com.buzzware.nowapp.R;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.cjt2325.cameralibrary.listener.RecordStateListener;
import com.cjt2325.cameralibrary.util.FileUtil;
import java.io.File;

/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor
 * @Date 2018/8/22 10:54
 * @description 视频拍摄界面
 */
public class JCCameraActivity extends Base1 {

    @BindView(R.id.jcameraview)
    JCameraView mJCameraView;

    @BindView(R.id.sSecCard)
    CardView sSecCard;

    @BindView(R.id.tSecCard)
    CardView tSecCard;

    @BindView(R.id.fSecCard)
    CardView fSecCard;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_jc_camera;
    }

    @Override
    protected void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void initView() {
        //设置视频保存路径
        mJCameraView.setSaveVideoPath(
                Environment.getExternalStorageDirectory().getPath() + File.separator
                        + "videoeditor" + File.separator + "small_video");
        mJCameraView.setMinDuration(1000); //设置最短录制时长
        mJCameraView.setDuration(10000); //设置最长录制时长
        mJCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        mJCameraView.setTip("");
        mJCameraView.setRecordShortTip("");
        mJCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        mJCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.d("CJT", "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {

                Toast.makeText(JCCameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        mJCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
//                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
                String path = FileUtil.saveBitmap("small_video", bitmap);
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                String path = FileUtil.saveBitmap("small_video", firstFrame);
                //url:/storage/emulated/0/haodiaoyu/small_video/video_1508930416375.mp4, Bitmap:/storage/emulated/0/haodiaoyu/small_video/picture_1508930429832.jpg
                Log.d("CJT", "url:" + url + ", firstFrame:" + path);

                TrimVideoActivity.startActivity(JCCameraActivity.this, url);
                finish();
            }
        });
        mJCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        mJCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(JCCameraActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });
        mJCameraView.setRecordStateListener(new RecordStateListener() {
            @Override
            public void recordStart() {

            }

            @Override
            public void recordEnd(long time) {


            }

            @Override
            public void recordCancel() {

            }
        });
        select30Second();
//        mJCameraView.setDuration(3000); //设置最长录制时长

    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }


    int count = 0;

    public void select60Sec(View v) {
        fSecCard.setVisibility(View.INVISIBLE);
        sSecCard.setVisibility(View.VISIBLE);
        tSecCard.setVisibility(View.INVISIBLE);

        mJCameraView.setDuration(60 * 1000);
    }

    public void select15Sec(View v) {

        sSecCard.setVisibility(View.INVISIBLE);
        tSecCard.setVisibility(View.INVISIBLE);
        fSecCard.setVisibility(View.VISIBLE);

        mJCameraView.setDuration(15 * 1000);
    }

    public void select30Sec(View v) {

        fSecCard.setVisibility(View.INVISIBLE);
        sSecCard.setVisibility(View.INVISIBLE);
        tSecCard.setVisibility(View.VISIBLE);

        mJCameraView.setDuration(30 * 1000);
    }

     void select30Second() {

        fSecCard.setVisibility(View.INVISIBLE);
        sSecCard.setVisibility(View.INVISIBLE);
        tSecCard.setVisibility(View.VISIBLE);

        mJCameraView.setDuration(30 * 1000);
    }


    @Override
    public void onResume() {
        super.onResume();
        mJCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mJCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
