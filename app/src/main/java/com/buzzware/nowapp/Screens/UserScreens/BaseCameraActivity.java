package com.buzzware.nowapp.Screens.UserScreens;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLException;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.buzzware.nowapp.Libraries.libactivities.TrimVideoActivity;
import com.buzzware.nowapp.Libraries.widget.SampleGLView;
import com.buzzware.nowapp.R;
import com.daasuu.camerarecorder.CameraRecordListener;
import com.daasuu.camerarecorder.CameraRecorder;
import com.daasuu.camerarecorder.CameraRecorderBuilder;
import com.daasuu.camerarecorder.LensFacing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by sudamasayuki2 on 2018/07/02.
 */

public class BaseCameraActivity extends AppCompatActivity {

    private SampleGLView sampleGLView;
    protected CameraRecorder cameraRecorder;
    private String filepath;
    private TextView recordBtn;
    private TextView secLeft;
    protected LensFacing lensFacing = LensFacing.BACK;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    ImageView holdBtn;
    protected int videoWidth = 720;
    protected int videoHeight = 720;
    CardView tSecCard, sSecCard, fSecCard;
    private AlertDialog filterDialog;
    private boolean toggleClick = false;
    CountDownTimer timer;

    int sec = 10;

    protected void onCreateActivity() {

        sSecCard = findViewById(R.id.sSecCard);
        tSecCard = findViewById(R.id.tSecCard);
        fSecCard = findViewById(R.id.fSecCard);
        secLeft = findViewById(R.id.secLeft);
        holdBtn = findViewById(R.id.holdView);
        recordBtn = findViewById(R.id.tapToRecordTV);
        recordBtn.setText(getString(R.string.app_record));
        select30Sec();
        recordBtn.setText(getString(R.string.app_record));
        holdBtn.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                filepath = getVideoFilePath();
                cameraRecorder.start(filepath);
                recordBtn.setText("Stop");
                setAndStartTimer();

                holdBtn.setImageResource(R.drawable.red_round);
                secLeft.setText("");
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                cameraRecorder.stop();
                holdBtn.setImageResource(R.drawable.rounder_circle_gray_stoke);
                recordBtn.setText(getString(R.string.app_record));
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                secLeft.setText("");
                return true;
            }

            return false;
        });

//        findViewById(R.id.btn_flash).setOnClickListener(v -> {
//            if (cameraRecorder != null && cameraRecorder.isFlashSupport()) {
//                cameraRecorder.switchFlashMode();
//                cameraRecorder.changeAutoFocus();
//            }
//        });

        findViewById(R.id.rotateCamera).setOnClickListener(v -> {
            releaseCamera();
            if (lensFacing == LensFacing.BACK) {
                lensFacing = LensFacing.FRONT;
            } else {
                lensFacing = LensFacing.BACK;
            }
            toggleClick = true;
        });

    }

    int count = 0;

    public void select60Sec(View v) {
        fSecCard.setVisibility(View.GONE);
        sSecCard.setVisibility(View.VISIBLE);
        tSecCard.setVisibility(View.GONE);
        sec = 60;
    }

    public void select15Sec(View v) {
        sSecCard.setVisibility(View.GONE);
        tSecCard.setVisibility(View.GONE);
        fSecCard.setVisibility(View.VISIBLE);
        sec = 15;
    }

    public void select30Sec(View v) {
        fSecCard.setVisibility(View.GONE);
        sSecCard.setVisibility(View.GONE);
        tSecCard.setVisibility(View.VISIBLE);
        sec = 30;
    }

    public void select30Sec() {
        fSecCard.setVisibility(View.GONE);
        sSecCard.setVisibility(View.GONE);
        tSecCard.setVisibility(View.VISIBLE);
        sec = 30;
    }

    void setAndStartTimer() {
        if (timer != null)
            timer.cancel();
        timer = null;
        count = 0;
        timer = new CountDownTimer(sec * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                count = count + 1;
                secLeft.setText(String.valueOf(count) + " sec");

            }

            public void onFinish() {
                count = 0;
                cameraRecorder.stop();
                recordBtn.setText(getString(R.string.app_record));
                if (timer != null)
                    timer.cancel();
            }
        };

        timer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (cameraRecorder != null) {
            cameraRecorder.stop();
            cameraRecorder.release();
            cameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.wrap_view)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(() -> {
            FrameLayout frameLayout = findViewById(R.id.wrap_view);
            frameLayout.removeAllViews();
            sampleGLView = null;
            sampleGLView = new SampleGLView(getApplicationContext());
            sampleGLView.setTouchListener((event, width, height) -> {
                if (cameraRecorder == null) return;
                cameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
            });
            frameLayout.addView(sampleGLView);
        });
    }


    private void setUpCamera() {
        setUpCameraView();

        cameraRecorder = new CameraRecorderBuilder(this, sampleGLView)
                .recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(boolean flashSupport) {
                        runOnUiThread(() -> {
                            findViewById(R.id.flashIV).setEnabled(flashSupport);
                        });
                    }

                    @Override
                    public void onRecordComplete() {
                        exportMp4ToGallery(getApplicationContext(), filepath);
                        TrimVideoActivity.startActivity(BaseCameraActivity.this, filepath);
                    }

                    @Override
                    public void onRecordStart() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("CameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(() -> {
                                setUpCamera();
                            });
                        }
                        toggleClick = false;
                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();

    }

//    private void changeFilter(Filters filters) {
//        cameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
//    }


    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(() -> {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
            Bitmap snapshotBitmap = createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

            runOnUiThread(() -> {
                bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
            });
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    public static String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.mp4";
    }

    public static File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }


    public static File getAndroidDCIMFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getImageFilePath() {
        return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.png";
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

}