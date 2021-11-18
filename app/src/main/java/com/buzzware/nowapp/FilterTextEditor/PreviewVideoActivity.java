package com.buzzware.nowapp.FilterTextEditor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buzzware.nowapp.Addapters.FiltersAdapter;
import com.buzzware.nowapp.FilterTextEditor.Utils.DimensionData;
import com.buzzware.nowapp.FilterTextEditor.photoeditor.OnPhotoEditorListener;
import com.buzzware.nowapp.FilterTextEditor.photoeditor.PhotoEditor;
import com.buzzware.nowapp.FilterTextEditor.photoeditor.PhotoEditorView;
import com.buzzware.nowapp.FilterTextEditor.photoeditor.SaveSettings;
import com.buzzware.nowapp.FilterTextEditor.photoeditor.TextStyleBuilder;
import com.buzzware.nowapp.FilterTextEditor.photoeditor.ViewType;
import com.buzzware.nowapp.Libraries.libactivities.TrimVideoActivity;
import com.buzzware.nowapp.Libraries.utils.ExtractVideoInfoUtil;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.UploadPostScreen;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlContrastFilter;
import com.daasuu.mp4compose.filter.GlFilterGroup;
import com.daasuu.mp4compose.filter.GlHueFilter;
import com.daasuu.mp4compose.filter.GlSepiaFilter;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION;
import static com.buzzware.nowapp.FilterTextEditor.Utils.UtilsNew.getScaledDimension;
import static com.buzzware.nowapp.Screens.UserScreens.BaseCameraActivity.getAndroidDCIMFolder;
import static com.buzzware.nowapp.Screens.UserScreens.BaseCameraActivity.getAndroidImageFolder;
import static com.buzzware.nowapp.Screens.UserScreens.BaseCameraActivity.getAndroidMoviesFolder;

public class PreviewVideoActivity extends AppCompatActivity implements OnPhotoEditorListener, PropertiesBSFragment.Properties,
        View.OnClickListener,
        StickerBSFragment.StickerListener {

    private static final String TAG = PreviewVideoActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private PhotoEditor mPhotoEditor;
    private String globalVideoUrl = "";
    private PropertiesBSFragment propertiesBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private MediaPlayer mediaPlayer;
    private String videoPath = "";
    private String imagePath = "";
    private ArrayList<String> exeCmd;
    FFmpeg fFmpeg;
    private String[] newCommand;
    private ProgressDialog progressDialog;

    String thumbPath;

    private int originalDisplayWidth;
    private int originalDisplayHeight;
    private int newCanvasWidth, newCanvasHeight;
    private int DRAW_CANVASW = 0;
    private int DRAW_CANVASH = 0;

    private TextureView videoSurface;
    private PhotoEditorView ivImage;
    private RecyclerView filtersRV;
    private ImageView imgClose;
    private ImageView imgUndo;
    private ImageView imgText;
    private ImageView imgDraw;
    private ImageView imgSticker;
    private ImageView imgDelete;
    private ImageView filterIV;
    private ImageView imgDone;

    String selectedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview_video_view_video_editor);
        initView();
        filtersRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        filtersRV.setAdapter(new FiltersAdapter(this, position -> onItemSelected(position)));
        initViews();
        ivImage.setOnClickListener(view -> filtersRV.setVisibility(View.GONE));
//        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
//        Glide.with(this).load(getIntent().getStringExtra("DATA")).into(ivImage.getSource());
        Glide.with(this).load(R.drawable.trans).into(ivImage.getSource());

        videoPath = getIntent().getStringExtra("DATA");
        thumbPath = getIntent().getStringExtra("thumb");
        selectedPath = videoPath;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        String metaRotation = retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION);

        int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);

        if (rotation == 90 || rotation == 270) {
            DRAW_CANVASH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            DRAW_CANVASW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } else {
            DRAW_CANVASW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            DRAW_CANVASH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        }
        setCanvasAspectRatio();
//
//        videoSurface.getLayoutParams().width = newCanvasWidth;
//        videoSurface.getLayoutParams().height = newCanvasHeight;

//        ivImage.getLayoutParams().width = newCanvasWidth;
//        ivImage.getLayoutParams().height = newCanvasHeight;

        Log.d(">>", "width>> " + newCanvasWidth + "height>> " + newCanvasHeight + " rotation >> " + rotation);

    }

    public void onItemSelected(int position) {

        mediaComposer(position);
    }

    private void initViews() {
        fFmpeg = FFmpeg.getInstance(this);
        progressDialog = new ProgressDialog(this);
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        propertiesBSFragment = new PropertiesBSFragment();
        propertiesBSFragment.setPropertiesChangeListener(this);
        mPhotoEditor = new PhotoEditor.Builder(this, ivImage)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .setDeleteView(imgDelete)
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        imgClose.setOnClickListener(this);
        filterIV.setOnClickListener(this);
        imgDone.setOnClickListener(this);
        imgDraw.setOnClickListener(this);
        imgText.setOnClickListener(this);
        imgUndo.setOnClickListener(this);
        imgSticker.setOnClickListener(this);
//        findViewById(R.id.containerCV).setOnClickListener(view -> filtersRV.setVisibility(View.GONE));
        findViewById(R.id.overlayVw).setOnClickListener(view -> hideOrDisplayFiltersRV());
//        videoSurface.setOnClickListener(view -> filtersRV.setVisibility(View.GONE));
//        mPhotoEditor.setOnClickListener(view -> filtersRV.setVisibility(View.GONE));
    }

    private void hideOrDisplayFiltersRV() {

        if (filtersRV.getVisibility() == View.GONE) {
            filtersRV.setVisibility(View.VISIBLE);

            findViewById(R.id.overlayVw).setVisibility(View.VISIBLE);
        } else {
            filtersRV.setVisibility(View.GONE);
            findViewById(R.id.overlayVw).setVisibility(View.GONE);

        }
    }


    private void setUpVideoSurface() {

        if (mediaPlayer != null) {

            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();

                mediaPlayer = null;

//                videoSurface.

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        videoSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

                Surface surface = new Surface(surfaceTexture);

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );

                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(selectedPath));
                    mediaPlayer.setSurface(surface);
//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mediaPlayer) {
//                            mediaPlayer.start();
//                        }
//                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d(TAG, "onSurfaceTextureAvailable: Exception" + e.getLocalizedMessage());
                }

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                Log.d(TAG, "onSurfaceTextureSizeChanged: ");

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "onSurfaceTextureDestroyed: ");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "onSurfaceTextureUpdated: ");

            }
        });

        exeCmd = new ArrayList<>();
        try {
            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.d("binaryLoad", "onFailure");

                }

                @Override
                public void onSuccess() {
                    Log.d("binaryLoad", "onSuccess");
                }

                @Override
                public void onStart() {
                    Log.d("binaryLoad", "onStart");

                }

                @Override
                public void onFinish() {
                    Log.d("binaryLoad", "onFinish");

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void executeCommand(String[] command, final String absolutePath) {
        try {
            fFmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String s) {

                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {

                    }

//                    ExtractVideoInfoUtil mExtractVideoInfoUtil = new ExtractVideoInfoUtil(selectedPath);
//                    Bitmap bitmap = mExtractVideoInfoUtil.extractFrame();
//
//                    String firstFrame = FileUtil.saveBitmap("small_video", bitmap);
//                    if (bitmap != null && !bitmap.isRecycled()) {
//                        bitmap.recycle();
//                        bitmap = null;
//                    }

                    Log.d("CommandExecute", "onSuccess" + "  " + s);
                    Toast.makeText(getApplicationContext(), "Sucess", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PreviewVideoActivity.this, UploadPostScreen.class);
                    i.putExtra("path", absolutePath);
                    i.putExtra("thumb", thumbPath);
                    startActivity(i);
                    finish();

                }

                @Override
                public void onProgress(String s) {
                    progressDialog.setMessage(s);
                    Log.d("CommandExecute", "onProgress" + "  " + s);

                }

                @Override
                public void onFailure(String s) {
                    Log.d("CommandExecute", "onFailure" + "  " + s);
                    progressDialog.hide();

                }

                @Override
                public void onStart() {
                    progressDialog.setTitle("Processing");
                    progressDialog.setMessage("Starting");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    progressDialog.hide();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.filterIV:

                hideOrDisplayFiltersRV();
                break;
            case R.id.doneBt:
                saveImage();
                break;
            case R.id.imgDraw:
                setDrawingMode();
                break;
            case R.id.imgText:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(PreviewVideoActivity.this, 0);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {

                    @Override
                    public void onDone(String inputText, int colorCode, int position) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);
                        Typeface typeface = ResourcesCompat.getFont(PreviewVideoActivity.this, TextEditorDialogFragment.getDefaultFontIds(PreviewVideoActivity.this).get(position));
                        styleBuilder.withTextFont(typeface);
                        mPhotoEditor.addText(inputText, styleBuilder, position);
                    }
                });
                break;
            case R.id.imgUndo:
                Log.d("canvas>>", mPhotoEditor.undoCanvas() + "");
                mPhotoEditor.clearBrushAllViews();
                break;
            case R.id.imgSticker:
                try {
                    mStickerBSFragment.dismiss();//to hide it
                    mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void setCanvasAspectRatio() {

        originalDisplayHeight = getDisplayHeight();
        originalDisplayWidth = getDisplayWidth();

//        DimensionData displayDiamenion =
//                getScaledDimension(new DimensionData((int) DRAW_CANVASW, (int) DRAW_CANVASH),
//                        new DimensionData(originalDisplayWidth, originalDisplayHeight));
        newCanvasWidth = getDisplayWidth();
        newCanvasHeight = getDisplayHeight();

    }


    private void setDrawingMode() {
        if (mPhotoEditor.getBrushDrawableMode()) {
            mPhotoEditor.setBrushDrawingMode(false);
            imgDraw.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp));
        } else {
            mPhotoEditor.setBrushDrawingMode(true);
            imgDraw.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            propertiesBSFragment.show(getSupportFragmentManager(), propertiesBSFragment.getTag());
        }
    }


    public String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "filter_applyImage.png";
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {

//        imagePath = getVideoFilePath();
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + ""
                + System.currentTimeMillis() + ".png");
        try {
            file.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(false)
                    .build();

            mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    PreviewVideoActivity.this.imagePath = imagePath;
                    Log.d("imagePath>>", imagePath);
                    Log.d("imagePath2>>", Uri.fromFile(new File(imagePath)).toString());
                    ivImage.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                    Toast.makeText(PreviewVideoActivity.this, "Saved successfully...", Toast.LENGTH_SHORT).show();
                    applayWaterMark();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(PreviewVideoActivity.this, "Saving Failed...", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    private void mediaComposer(int selectedIndex) {

        final String outputPath = getVideoFilePath();

        GlFilterGroup filterGroup = new GlFilterGroup();


        if (selectedIndex == 0) {

            selectedPath = videoPath;

//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(selectedPath);
//                            setUpVideoSurface();
            if (mediaPlayer != null) {

                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
//                                    mediaPlayer.release();

//                                    mediaPlayer = null;

//                videoSurface.

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {

                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(selectedPath));
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "onSurfaceTextureAvailable: Exception" + e.getLocalizedMessage());
            }
            return;
        } else if (selectedIndex == 1) {

            filterGroup = new GlFilterGroup(new GlContrastFilter());

        } else if (selectedIndex == 2) {

            filterGroup = new GlFilterGroup(new GlSepiaFilter());

        } else if (selectedIndex == 3) {

            filterGroup = new GlFilterGroup(new GlHueFilter());

        } else if (selectedIndex == 4) {

            filterGroup = new GlFilterGroup(new GlContrastFilter());

        } else if (selectedIndex == 5) {

            filterGroup = new GlFilterGroup(new GlHueFilter());

        }

        selectedPath = outputPath;

        UIUpdate.destroy();

        UIUpdate.GetUIUpdate(this).setProgressDialog("Converting", "Please Wait", PreviewVideoActivity.this);

        new Mp4Composer(videoPath, outputPath)
//                .rotation(Rotation.ROTATION_90)
//                .size((width) 540, (height) 960)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)

                .filter(filterGroup)
//                .trim((trimStartMs) 200, (trimEndMs) 5000)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d(TAG, "onProgress = " + progress);
                    }

                    @Override
                    public void onCurrentWrittenVideoTime(long timeUs) {

                    }

                    @Override
                    public void onCompleted() {

                        UIUpdate.GetUIUpdate(PreviewVideoActivity.this).DismissProgressDialog();

                        Log.d(TAG, "onCompleted()");
                        runOnUiThread(() -> {
//                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                            retriever.setDataSource(selectedPath);
//                            setUpVideoSurface();
                            if (mediaPlayer != null) {

                                try {
                                    mediaPlayer.stop();
                                    mediaPlayer.reset();
//                                    mediaPlayer.release();

//                                    mediaPlayer = null;

//                videoSurface.

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
//                                mediaPlayer = new MediaPlayer();
//                                mediaPlayer.setAudioAttributes(
//                                        new AudioAttributes.Builder()
//                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                                .setUsage(AudioAttributes.USAGE_MEDIA)
//                                                .build()
//                                );

                                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(selectedPath));
//                                mediaPlayer.setSurface(surface);
//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mediaPlayer) {
//                            mediaPlayer.start();
//                        }
//                    });
//                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                    @Override
//                                    public void onCompletion(MediaPlayer mediaPlayer) {
//                                        mediaPlayer.start();
//                                    }
//                                });
                                mediaPlayer.prepare();
                                mediaPlayer.start();

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d(TAG, "onSurfaceTextureAvailable: Exception" + e.getLocalizedMessage());
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

//                        UIUpdate.Get .UIUpdate(TrimVideoActivity.this).DismissProgressDialog();

                        Log.e(TAG, "onFailed()", exception);
                    }
                })
                .start();
    }

    public String getVideoFilePath1() {
        return getAndroidDCIMFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "filter_apply.mp4";
    }

    private void applayWaterMark() {

//        imagePath = generatePath(Uri.fromFile(new File(imagePath)),PreviewVideoActivity.this);

//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(videoPath);
//        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        /*if (width > height) {
            int tempWidth = width;
            width = height;
            height = tempWidth;
        }*/

//        Log.d(">>", "width>> " + width + "height>> " + height);
//        retriever.release();

        File output = new File(Environment.getExternalStorageDirectory()
                + File.separator + ""
                + System.currentTimeMillis() + ".mp4");
        try {
            output.createNewFile();

            exeCmd.add("-y");
            exeCmd.add("-i");
            exeCmd.add(videoPath);
//            exeCmd.add("-framerate 30000/1001 -loop 1");
            exeCmd.add("-i");
            exeCmd.add(imagePath);
            exeCmd.add("-filter_complex");
//            exeCmd.add("-strict");
//            exeCmd.add("-2");
//            exeCmd.add("-map");
//            exeCmd.add("[1:v]scale=(iw+(iw/2)):(ih+(ih/2))[ovrl];[0:v][ovrl]overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2");
//            exeCmd.add("[1:v]scale=720:1280:1823[ovrl];[0:v][ovrl]overlay=x=0:y=0");
            exeCmd.add("[1:v]scale=" + DRAW_CANVASW + ":" + DRAW_CANVASH + "[ovrl];[0:v][ovrl]overlay=x=0:y=0");
            exeCmd.add("-c:v");
            exeCmd.add("libx264");
            exeCmd.add("-preset");
            exeCmd.add("ultrafast");
            exeCmd.add(output.getAbsolutePath());


            newCommand = new String[exeCmd.size()];
            for (int j = 0; j < exeCmd.size(); j++) {
                newCommand[j] = exeCmd.get(j);
            }


            for (int k = 0; k < newCommand.length; k++) {
                Log.d("CMD==>>", newCommand[k] + "");
            }

//            newCommand = new String[]{"-i", videoPath, "-i", imagePath, "-preset", "ultrafast", "-filter_complex", "[1:v]scale=2*trunc(" + (width / 2) + "):2*trunc(" + (height/ 2) + ") [ovrl], [0:v][ovrl]overlay=0:0" , output.getAbsolutePath()};
            executeCommand(newCommand, output.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.setBrushDrawingMode(false);
        imgDraw.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp));
        mPhotoEditor.addImage(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpVideoSurface();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null) {

            try {
                mediaPlayer.release();

                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode, final int position) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode, position);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode, int position) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);
                Typeface typeface = ResourcesCompat.getFont(PreviewVideoActivity.this, TextEditorDialogFragment.getDefaultFontIds(PreviewVideoActivity.this).get(position));
                styleBuilder.withTextFont(typeface);
                mPhotoEditor.editText(rootView, inputText, styleBuilder, position);
            }
        });
    }

    public String generatePath(Uri uri, Context context) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            filePath = generateFromKitkat(uri, context);
        }

        if (filePath != null) {
            return filePath;
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? uri.getPath() : filePath;
    }

    @TargetApi(19)
    private String generateFromKitkat(Uri uri, Context context) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Video.Media.DATA};
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        }
        return filePath;
    }

    private int getDisplayWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getDisplayHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {

    }

    @Override
    public void onBrushSizeChanged(int brushSize) {

    }


    private void initView() {
        videoSurface = (TextureView) findViewById(R.id.videoSurface);
        ivImage = (PhotoEditorView) findViewById(R.id.ivImage);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgUndo = (ImageView) findViewById(R.id.imgUndo);
        filtersRV = (RecyclerView) findViewById(R.id.filtersListRV);
        imgText = (ImageView) findViewById(R.id.imgText);
        imgDraw = (ImageView) findViewById(R.id.imgDraw);
        imgSticker = (ImageView) findViewById(R.id.imgSticker);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        imgDone = (ImageView) findViewById(R.id.doneBt);
        filterIV = (ImageView) findViewById(R.id.filterIV);
    }
}
