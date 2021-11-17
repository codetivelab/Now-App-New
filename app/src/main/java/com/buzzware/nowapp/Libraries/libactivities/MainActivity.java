package com.buzzware.nowapp.Libraries.libactivities;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.buzzware.nowapp.Libraries.base.BaseActivity;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.JCCameraActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static java.security.AccessController.getContext;

public class MainActivity extends BaseActivity {

    private RxPermissions mRxPermissions;
    private static final int RECORD_VIDEO_REQUEST = 1000;
    File mVideoFile, mThumbnailFile;

    static final String FILE_PREFIX = "recorder-";
    static final String THUMBNAIL_FILE_EXTENSION = "jpg";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mRxPermissions = new RxPermissions(this);
    }

    /**
     * 拍照
     * //     * @param view
     */

    public void startActivity(File videoFile, File thumbnailFile) {

    }

    public void takeCamera(View view) {
        mRxPermissions
                .request(permission.WRITE_EXTERNAL_STORAGE, permission.RECORD_AUDIO, permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) { //已获取权限

//                            createTempFiles();

                            Intent intent = new Intent(MainActivity.this, JCCameraActivity.class);
                            startActivityForResult(intent, 100);
                        } else {
                            Toast.makeText(MainActivity.this, "给点权限行不行？", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 相册
     *
     * @param view
     */
    public void takeAlbum(View view) {
        mRxPermissions
                .request(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) { //已获取权限
                            Intent intent = new Intent(MainActivity.this, VideoAlbumActivity.class);
                            startActivityForResult(intent, 100);
                        } else {
                            Toast.makeText(MainActivity.this, "给点权限行不行？", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
