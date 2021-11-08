package com.buzzware.nowapp.Screens.General.filters;
//
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.databinding.DataBindingUtil;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.buzzware.nowapp.R;
//import com.buzzware.nowapp.Screens.General.filters.FilterTypes.CustomFilterTypes;
//import com.buzzware.nowapp.Screens.UserScreens.FilterAdjuster;
//import com.buzzware.nowapp.UIUpdates.UIUpdate;
//import com.buzzware.nowapp.databinding.ActivityFilterBinding;
//import com.google.android.exoplayer2.MediaItem;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//
//import java.util.List;
//
//public class FilterActivity extends AppCompatActivity{
////        extends AppCompatActivity implements FilterSelectedListener {
////
////    private GPUPlayerView gpuPlayerView;
////
////    ActivityFilterBinding binding;
////
////    String filePath;
////
////    private GlFilter filter;
////
////    private FilterAdjuster adjuster;
////
////    private SimpleExoPlayer player;
////
////    final List<CustomFilterTypes> filterTypes = CustomFilterTypes.createFilterList();
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        binding = DataBindingUtil.setContentView(FilterActivity.this, R.layout.activity_filter);
////        getExtrasFromIntent();
////        setUpViews();
////        setUpSeekBar();
////        setVideoView();
////        setFiltersList();
////        setListeners();
////    }
////
////    private void setListeners() {
////
////        binding.applyBn.setOnClickListener(v -> applyFilter());
////    }
////
////    private void getExtrasFromIntent() {
////        Bundle b = getIntent().getExtras();
////
////        if (b.getString("path") != null) {
////            filePath = b.getString("path");
////        }
////    }
////
////    private void setUpSeekBar() {
////
////        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
////            @Override
////            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                if (adjuster != null) {
////                    adjuster.adjust(filter, progress);
////                }
////            }
////
////            @Override
////            public void onStartTrackingTouch(SeekBar seekBar) {
////
////            }
////
////            @Override
////            public void onStopTrackingTouch(SeekBar seekBar) {
////
////            }
////        });
////    }
////
////    private void setUpViews() {
////
////
////    }
////
////    private void setVideoView() {
////
////    }
////
////    private void setFiltersList() {
////
////        binding.filtersListRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
////        binding.filtersListRV.setAdapter(new FiltersRecyclerViewAdapter(this));
////    }
////
////    @Override
////    protected void onResume() {
////        super.onResume();
////
////        setUpSimpleExoPlayer();
////        setUoGlPlayerView();
////        setUpTimer();
////
////        player.setPlayWhenReady(true);
////
////        onFilterSelected(0);
////    }
////
////    @Override
////    protected void onPause() {
////        super.onPause();
////
////        releasePlayer();
////    }
////
////
////    void applyFilter() {
////
////        UIUpdate.GetUIUpdate(this).setProgressDialog("Applying Filter", "Please Wait", this);
////
////        String destinationPath = filePath.substring(0, filePath.toCharArray().length - 5);
////
////        new GPUMp4Composer(filePath, filePath)
//////                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
////                .filter(filter)
////                .listener(new GPUMp4Composer.Listener() {
////                    @Override
////                    public void onProgress(double progress) {
////
////
////                    }
////
////                    @Override
////                    public void onCompleted() {
////
////                        UIUpdate.GetUIUpdate(FilterActivity.this).DismissProgressDialog();
////
////                        UIUpdate.GetUIUpdate(FilterActivity.this).ShowToastMessage("Successfully Applied");
////
////                    }
////
////                    @Override
////                    public void onCanceled() {
////
////                        UIUpdate.GetUIUpdate(FilterActivity.this).ShowToastMessage("Cancelled");
////                    }
////
////                    @Override
////                    public void onFailed(Exception exception) {
////
////                        UIUpdate.GetUIUpdate(FilterActivity.this).AlertDialog("Alert", exception.getLocalizedMessage());
////
////                    }
////                })
////                .start();
////    }
////
////    private void setUpSimpleExoPlayer() {
////        // SimpleExoPlayer
////        player = new SimpleExoPlayer.Builder(this)
////                .setTrackSelector(new DefaultTrackSelector(this))
////                .build();
////
////        player.addMediaItem(MediaItem.fromUri(Uri.parse(filePath)));
////        player.prepare();
////        player.setPlayWhenReady(true);
////    }
////
////
////    private void setUoGlPlayerView() {
////        gpuPlayerView = new GPUPlayerView(this);
////        gpuPlayerView.setSimpleExoPlayer(player);
////        gpuPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
////
////        binding.layoutMovieWrapper.addView(gpuPlayerView);
////
////        gpuPlayerView.onResume();
////    }
////
////
////    private void setUpTimer() {
//////        playerTimer = new PlayerTimer();
//////        playerTimer.setCallback(new PlayerTimer.Callback() {
//////            @Override
//////            public void onTick(long timeMillis) {
//////                long position = player.getCurrentPosition();
//////                long duration = player.getDuration();
//////
//////                if (duration <= 0) return;
//////
//////                timeSeekBar.setMax((int) duration / 1000);
//////                timeSeekBar.setProgress((int) position / 1000);
//////            }
//////        });
//////        playerTimer.start();
////    }
////
////
////    private void releasePlayer() {
////        gpuPlayerView.onPause();
////        binding.layoutMovieWrapper.removeAllViews();
////        gpuPlayerView = null;
////        player.stop();
////        player.release();
////        player = null;
////    }
////
////
////    @Override
////    public void onFilterSelected(int position) {
////
////        filter = CustomFilterTypes.createGlFilter(filterTypes.get(position), getApplicationContext());
////
////        adjuster = CustomFilterTypes.createFilterAdjuster(filterTypes.get(position));
////
////        gpuPlayerView.setGlFilter(filter);
////    }
//}
