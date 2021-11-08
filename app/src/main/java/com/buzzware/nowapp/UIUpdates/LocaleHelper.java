package com.buzzware.nowapp.UIUpdates;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sajeel on 12/24/2018.
 */

public class LocaleHelper {
    Context context;

    public LocaleHelper(Context context) {
        this.context = context;
    }

    public void SetLocale(String lng) {
        Locale locale= new Locale(lng);
        Locale.setDefault(locale);
        Configuration configuration= new Configuration();
        configuration.locale= locale;
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        SharedPreferences.Editor editor= context.getSharedPreferences("Setting", MODE_PRIVATE).edit();
        editor.putString("MyLang", lng);
        editor.apply();
        editor.commit();
    }

    public void getLocale()
    {
        SharedPreferences prof= context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String lng= prof.getString("MyLang", "");
        SetLocale(lng);
    }

    public String getLanguageLocale()
    {
        SharedPreferences prof= context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String lng= prof.getString("MyLang", "");
        return lng;
    }
}
