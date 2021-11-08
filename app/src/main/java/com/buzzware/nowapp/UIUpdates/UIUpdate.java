package com.buzzware.nowapp.UIUpdates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.buzzware.nowapp.R;


/**
 * Created by Sajeel on 12/24/2018.
 */

public class UIUpdate {

    private static ProgressDialog progressDialog;

    Dialog customDialog;
    Context context;
    Dialog myDialog;

    public static UIUpdate uiUpdate;

    public static UIUpdate GetUIUpdate(Context context) {
        if (uiUpdate == null) {
            uiUpdate = new UIUpdate(context);
        }
        return uiUpdate;
    }

    public static void destroy() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        uiUpdate = null;
    }

    public UIUpdate(Context context) {
        this.context = context;
        customDialog = new Dialog(context);
        myDialog = new Dialog(context);
    }

    public void GetFullScreenView(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public Drawable CustomDrawables() {
        Drawable customErrorDrawable = context.getResources().getDrawable(R.drawable.ic_baseline_error_24);
        customErrorDrawable.setBounds(0, 0, customErrorDrawable.getIntrinsicWidth(), customErrorDrawable.getIntrinsicHeight());
        return customErrorDrawable;
    }

    public void ShowAlertDialog(String title, String message, Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

//    public void ShowOkPopUp(String title, String message)
//    {
//        TextView btnok;
//        TextView titletv, messagetv;
//        myDialog.setContentView(R.layout.customdialog_ok);
//        btnok = (TextView) myDialog.findViewById(R.id.btnok);
//        titletv= (TextView) myDialog.findViewById(R.id.titletv);
//        messagetv= (TextView) myDialog.findViewById(R.id.messagetv);
//        titletv.setText(title);
//        messagetv.setText(message);
//        btnok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myDialog.dismiss();
//            }
//        });
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.show();
//    }

//    public void ShowPopUpNoInternet()
//    {
//        Button btnok;
//        myDialog.setContentView(R.layout.internetcustompopup);
//        btnok = (Button) myDialog.findViewById(R.id.btnok);
//        btnok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myDialog.dismiss();
//            }
//        });
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.setCancelable(false);
//        myDialog.show();
//    }

    public void AlertDialog(String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void setProgressDialog(String title, String message, Context context) {

        if (progressDialog != null && progressDialog.isShowing())
            return;

        progressDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        progressDialog.setProgressStyle(R.style.ProgressDailogStyle);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void DismissProgressDialog() {
        if(progressDialog == null)
            return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    public void ShowToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void ShowKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
