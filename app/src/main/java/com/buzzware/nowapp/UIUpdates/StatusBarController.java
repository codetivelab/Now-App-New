package com.buzzware.nowapp.UIUpdates;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.buzzware.nowapp.R;


/**
 * Created by Sajeel on 12/24/2018.
 */

public class StatusBarController {

    public static StatusBarController statusBarController;

    public static StatusBarController GetStatusBarController()
    {
        if(statusBarController == null)
        {
            statusBarController= new StatusBarController();
        }
        return statusBarController;
    }

    public void HideStatusBar(View decoderView)
    {
        decoderView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SetColoredStatusBar(Window window, Context activity)
    {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
    }
}
