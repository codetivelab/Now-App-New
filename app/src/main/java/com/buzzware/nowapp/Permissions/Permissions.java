package com.buzzware.nowapp.Permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by Sajeel on 12/24/2018.
 */

public class Permissions {
    Context context;

    public Permissions(Context context) {
        this.context = context;
    }

    public  boolean isStoragePermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public  boolean isPhoneCallPermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public  boolean isAgoraPermissionsGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)
                        == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public  boolean isTelephonePermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public  boolean isCameraPermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public  boolean isLocationPermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public  boolean isSmsPermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public  boolean isRecordAudioPermissionGranted() {
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }
}
