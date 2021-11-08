package com.buzzware.nowapp.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

import com.buzzware.nowapp.Models.BusinessModel;
import com.google.gson.Gson;

public class UserSessions {
    public static final String sessionkey= "USER_SESSION";
    public SharedPreferences preferences;
    public static final String FirebaseUserID= "FirebaseUserID";
    public static final String UserFirstNAME= "UserFirstNAME";
    public static final String UserLastNAME= "UserLastNAME";
    public static final String UserIMAGE= "UserImageUrl";
    public static final String UserEmail= "UserEmail";
    public static final String UserNumber= "UserNumber";
    public static final String UserType= "UserType";
    public static final String IsRemember= "IsRemember";

    //buisness sessions
    public static final String BUSINESS_MODEL = "BusinessModel";

    public static final String IsVideoUploading= "IsVideoUploading";

    public static UserSessions userSessions;

    public static UserSessions GetUserSession()
    {
        if(userSessions == null)
        {
            userSessions= new UserSessions();
        }
        return userSessions;
    }

    public void setIsVideoUploading(boolean isVideoUploading, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IsVideoUploading, isVideoUploading);
        editor.apply();
        editor.commit();
    }

    public boolean getIsRemember(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getBoolean(IsRemember, false);
    }

    public void setIsRemember(boolean userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IsRemember, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getUserNumber(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(UserNumber, "");
    }

    public void setUserNumber(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserNumber, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getUserType(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(UserType, "");
    }

    public void setUserType(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserType, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getUserLastNAME(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(UserLastNAME, "");
    }

    public void setUserLastNAME(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserLastNAME, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getUserEmail(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(UserEmail, "");
    }

    public void setUserEmail(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserEmail, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getFirebaseUserID(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(FirebaseUserID, "");
    }

    public void setFirebaseUserID(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FirebaseUserID, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getUserFirstNAME(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(UserFirstNAME, "");
    }

    public void setUserFirstNAME(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserFirstNAME, userStatus);
        editor.apply();
        editor.commit();
    }

    public String getUserIMAGE(Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        return preferences.getString(UserIMAGE, "");
    }

    public void setUserIMAGE(String userStatus, Context context) {
        preferences = context.getSharedPreferences(sessionkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserIMAGE, userStatus);
        editor.apply();
        editor.commit();
    }

    public void setBusiness(BusinessModel businessModel, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(businessModel);
        preferences = context.getSharedPreferences(BUSINESS_MODEL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BUSINESS_MODEL, json);
        editor.apply();
        editor.commit();
    }

    public BusinessModel getBusinessModel(Context context) {
        preferences = context.getSharedPreferences(BUSINESS_MODEL, Context.MODE_PRIVATE);
        String json = preferences.getString(BUSINESS_MODEL, "");
        return new Gson().fromJson(json, BusinessModel.class);
    }

}
