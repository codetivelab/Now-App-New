package com.buzzware.nowapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.UploadVideoResponseCallback;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.Sessions.UserSessions;

import java.io.File;

public class UploadVideoService extends Service {
    CharSequence CHANNEL_NAME = "UploadVideoInformation";
    String CHANNEL_DESCRIPTION = "Upload Video Information Notification";
    int UploadRecodingNotificationID= 103;
    String selectedVideoPath="", locationName="", latitude="", longitude="", thumbnail="";
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context= getApplicationContext();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        locationName = intent.getStringExtra(Constant.GetConstant().getUserLocation());
//        latitude = intent.getStringExtra(Constant.GetConstant().getUserLatitude());
//        longitude = intent.getStringExtra(Constant.GetConstant().getUserLongitude());
        thumbnail = intent.getStringExtra(Constant.GetConstant().getThumbnail());
        selectedVideoPath = intent.getStringExtra(context.getString(R.string.path));
        createNotification();
        UploadRecoding();
        return START_STICKY;
    }

    private void UploadRecoding() {
        final File file= new File(selectedVideoPath);
        Uri videoURI= Uri.fromFile(file);
        UserSessions.GetUserSession().setIsVideoUploading(true, context);
       // HomeFragment.SetCameraButton();
        FirebaseRequests.GetFirebaseRequests(context).UploadVideo(callback, videoURI, locationName, latitude, longitude, thumbnail, context);
    }

    UploadVideoResponseCallback callback= new UploadVideoResponseCallback() {
        @Override
        public void UploadVideoResponseListener(boolean isError, String message) {
            if(!isError){
                stopService();
                UserSessions.GetUserSession().setIsVideoUploading(false, context);
               // HomeFragment.SetCameraButton();
                CreateInformationNotification(context.getString(R.string.UploadVideo), message);
            }else{
                stopService();
                UserSessions.GetUserSession().setIsVideoUploading(false, context);
               // HomeFragment.SetCameraButton();
                CreateInformationNotification(context.getString(R.string.UploadVideo), message);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification() {
        Notification.Builder mBuilder = new Notification.Builder(
                getBaseContext());
        Notification notification;
        Intent notificationIntent = new Intent(getApplicationContext(), Long.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = mBuilder.setSmallIcon(R.drawable.now_logo).setTicker(getResources().getString(R.string.app_name)).setWhen(0)
                    .setAutoCancel(false)
                    .setCategory(Notification.EXTRA_BIG_TEXT)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("Video Uploading in progress...")
                    .setSound(null, null)
                    .setColor(ContextCompat.getColor(getBaseContext(),R.color.color_pink_primary))
                    .setStyle(new Notification.BigTextStyle()
                            .bigText("Video Uploading in progress..."))
                    .setChannelId("Uploading Video")
                    .setShowWhen(true)
                    .setOngoing(true)
                    .build();
        }else{
            notification = mBuilder.setSmallIcon((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)? R.drawable.now_logo:R.drawable.now_logo).setTicker(getResources().getString(R.string.app_name)).setWhen(0)
                    .setAutoCancel(false)
                    .setCategory(Notification.EXTRA_BIG_TEXT)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("Video Uploading in progress...")
                    .setSound(null, null)
                    .setColor(ContextCompat.getColor(getBaseContext(),R.color.dark_blue))
                    .setStyle(new Notification.BigTextStyle()
                            .bigText("Video Uploading in progress..."))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setShowWhen(true)
                    .setOngoing(true)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("Uploading Video", "Uploading", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(false);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        startForeground(1,notification); //for foreground service, don't use 0 as id. it will not work.
    }

    private void CreateInformationNotification(String title, String message) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.now_logo);
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this, getResources().getString(R.string.uploadRec))
                .setSmallIcon(R.drawable.now_logo)
                .setSound(soundUri)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_HIGH)
                .setLights(Color.GREEN, 500, 5000)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.uploadRec),
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.GREEN);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setName(getResources().getString(R.string.app_name));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(UploadRecodingNotificationID, mBuilder.build());
    }

    public void stopService() {
        Intent serviceIntent = new Intent(context, UploadVideoService.class);
        stopService(serviceIntent);
    }
}
