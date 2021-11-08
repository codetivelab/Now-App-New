package com.buzzware.nowapp.Screens.General;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.databinding.DataBindingUtil;
//
//import android.net.Uri;
//import android.os.Bundle;
//
//import com.buzzware.nowapp.R;
//import com.buzzware.nowapp.databinding.ActivityVideoBinding;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.source.MediaSource;
////import com.google.android.exoplayer2.source.ProgressiveMediaSource;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.TrackSelector;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.util.Util;
//
//public class VideoActivity extends AppCompatActivity {
//
//    ActivityVideoBinding binding;
//
//    SimpleExoPlayer absPlayerInternal;
//
//    // url of video which we are loading.
//    String videoURL = "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_video);
//
//        startStreaming();
//
//    }
//
//    private void startStreaming() {
//
//        TrackSelector trackSelectorDef = new DefaultTrackSelector();
////        absPlayerInternal = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef); //creating a player instance
//
//        String userAgent = Util.getUserAgent(this, this.getString(R.string.app_name));
//        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
//        Uri uriOfContentUrl = Uri.parse(videoURL);
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source
//
//        absPlayerInternal.prepare(mediaSource);
//        absPlayerInternal.setPlayWhenReady(true);
//
////        binding.exoplayerView.setPlayer(absPlayerInternal); // attach surface to the view
//
//        // start loading video and play it at the moment a chunk of it is available offline
////        binding.exoplayerView.setUseController(true);
////        binding.exoplayerView.setPlayer(absPlayerInternal);
//    }
//
//    private void pausePlayer(SimpleExoPlayer player) {
//        if (player != null) {
//            player.setPlayWhenReady(false);
//        }
//    }
//
//
//    private void stopPlayer(){
////        binding.exoplayerView.setPlayer(null);
////        absPlayer.release();
////        absPlayer = null;
//    }
//
//    private void seekTo(SimpleExoPlayer player, long positionInMS) {
//        if (player != null) {
//            player.seekTo(positionInMS);
//        }
//}}