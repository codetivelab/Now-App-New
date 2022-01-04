package com.buzzware.nowapp.Constants;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.buzzware.nowapp.Models.SelectionModel;
import com.buzzware.nowapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sajeel on 12/24/2018.
 */

public class Constant {

    public static String camera = "back";
    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyAV3MX38CxYlJuvEVwjXfKGFWUL3qt5LbA";

    private final int splashExpire = 4000;
    private String COUNTRY_CODE = "+92";
    private boolean IS_NETWORK_CONNECTED = false;
    private String ApiBaseUrl = "https://besttime.app/api/v1/forecasts";
    private String ContentType = "application/json; charset=utf-8";
    private String ApiFormat = "utf-8";
    private String NullString = "Null";
    private String StartBuisnessRating = "1";
    public static Constant constant;

    // api params
    private String api_key_private = "api_key_private";
    private String venue_name = "venue_name";
    private String venue_address = "venue_address";

    ///user types
    private String buisnessUser = "B";
    private String NormalUser = "N";

    //api resp fields
    private String status = "status";
    private String venue_info = "venue_info";

    ////firebase Collections
    private String UsersCollection = "Users";
    private String BuisnessDataCollection = "BusinessData";

    ////firebase user Documents
    private String UserFirstNameDocument = "userFirstName";
    private String UserLastNameDocument = "userLastName";
    private String UserEmailDocument = "userEmail";
    private String UserPhoneNumberDocument = "userPhoneNumber";
    private String UserImageURLDocument = "userImageUrl";
    private String UserPasswordDocument = "userPassword";
    private String UserTokenDocument = "userToken";
    private String UserTypeDocument = "userType";

    ////firebase business Documents
    private String BusinessNameDocument = "businessName";
    private String BuisnessTypeDocument = "businessType";
    private String BuisnessNumberDocument = "businessNumber";
    private String BuisnessAddressDocument = "businessAddress";
    private String BuisnessCityDocument = "businessCity";
    private String BuisnessStateDocument = "businessState";
    private String BuisnessZipCodeDocument = "businessZipcode";
    private String BuisnessLatitudeDocument = "businessLatitude";
    private String BuisnessLongitudeDocument = "businessLongitude";
    private String BuisnessLogoDocument = "businessLogo";
    private String BuisnessBackgroundImageDocument = "businessBackgroundImage";
    private String BuisnessLiscenceImageDocument = "businessLicenceImage";
    private String BuisnessVenueAddressDocument = "businessVenueAddress";
    private String BuisnessResponseDocument = "businessResponse";
    private String BuisnessTotalRatingDocument = "businessTotalRating";

    //storage folders
    private String ProfileImage = "profileImage";
    private String BuisnessLogo = "businessLogo";
    private String BuisnessLicense = "businessLicense";
    private String BusinessBackgroundImage = "businessBackgroundImage";

    public static final String STREAM_URL_MP4_VOD_SHORT = "http://demos.webmproject.org/exoplayer/glass.mp4";
    public static final String STREAM_URL_PORTLATE = "https://ccs3.akamaized.net/cchanclips/a6164c61eddb455190330e05c6c91ca6/clip.mp4";

    private String thumbnail = "thumbnail";


    public String getThumbnail() {
        return thumbnail;
    }

    public static Constant GetConstant() {
        if (constant == null) {
            constant = new Constant();
        }
        return constant;
    }


    public String getStartBuisnessRating() {
        return StartBuisnessRating;
    }

    public String getBuisnessTotalRatingDocument() {
        return BuisnessTotalRatingDocument;
    }

    public String getBuisnessVenueAddressDocument() {
        return BuisnessVenueAddressDocument;
    }

    public String getBuisnessResponseDocument() {
        return BuisnessResponseDocument;
    }

    public String getBuisnessUser() {
        return buisnessUser;
    }

    public String getNormalUser() {
        return NormalUser;
    }

    public String getUserTypeDocument() {
        return UserTypeDocument;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public String getBuisnessLogo() {
        return BuisnessLogo;
    }

    public String getBuisnessLicense() {
        return BuisnessLicense;
    }

    public String getBusinessBackgroundImage() {
        return BusinessBackgroundImage;
    }

    public String getUserPasswordDocument() {
        return UserPasswordDocument;
    }

    public String getUserTokenDocument() {
        return UserTokenDocument;
    }

    public String getNullString() {
        return NullString;
    }

    public String getUsersCollection() {
        return UsersCollection;
    }

    public String getBuisnessDataCollection() {
        return BuisnessDataCollection;
    }

    public String getUserFirstNameDocument() {
        return UserFirstNameDocument;
    }

    public String getUserLastNameDocument() {
        return UserLastNameDocument;
    }

    public String getUserEmailDocument() {
        return UserEmailDocument;
    }

    public String getUserPhoneNumberDocument() {
        return UserPhoneNumberDocument;
    }

    public String getUserImageURLDocument() {
        return UserImageURLDocument;
    }

    public String getBusinessNameDocument() {
        return BusinessNameDocument;
    }

    public String getBuisnessTypeDocument() {
        return BuisnessTypeDocument;
    }

    public String getBuisnessNumberDocument() {
        return BuisnessNumberDocument;
    }

    public String getBuisnessAddressDocument() {
        return BuisnessAddressDocument;
    }

    public String getBuisnessCityDocument() {
        return BuisnessCityDocument;
    }

    public String getBuisnessStateDocument() {
        return BuisnessStateDocument;
    }

    public String getBuisnessZipCodeDocument() {
        return BuisnessZipCodeDocument;
    }

    public String getBuisnessLatitudeDocument() {
        return BuisnessLatitudeDocument;
    }

    public String getBuisnessLongitudeDocument() {
        return BuisnessLongitudeDocument;
    }

    public String getBuisnessLogoDocument() {
        return BuisnessLogoDocument;
    }

    public String getBuisnessBackgroundImageDocument() {
        return BuisnessBackgroundImageDocument;
    }

    public String getBuisnessLiscenceImageDocument() {
        return BuisnessLiscenceImageDocument;
    }

    public String getVenue_info() {
        return venue_info;
    }

    public String getStatus() {
        return status;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public String getVenue_address() {
        return venue_address;
    }

    public String getApi_key_private() {
        return api_key_private;
    }

    public String getApiFormat() {
        return ApiFormat;
    }

    public String getContentType() {
        return ContentType;
    }

    public String GetGuestEmail(String timestamp) {
        return "guest" + timestamp + "@gmail.com";
    }

    public String GetGuestName(String timestamp) {
        return "Guest" + timestamp;
    }

    public String getApiBaseUrl() {
        return ApiBaseUrl;
    }

    public String getCOUNTRY_CODE() {
        return COUNTRY_CODE;
    }

    public int getSplashExpire() {
        return splashExpire;
    }

    public boolean getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info : networkInfo) {
            if (info.getTypeName().equalsIgnoreCase("WIFI")) {
                if (info.isConnected()) {
                    IS_NETWORK_CONNECTED = true;
                }
            }

            if (info.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (info.isConnected()) {
                    IS_NETWORK_CONNECTED = true;
                }
            }
        }
        return IS_NETWORK_CONNECTED;
    }

    public void DeleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public List<SelectionModel> GetBuisnessTypes(Context context) {
        List<SelectionModel> list = new ArrayList<>();
        list.add(new SelectionModel("", context.getString(R.string.restaurant)));
        list.add(new SelectionModel("", context.getString(R.string.nightClub)));
        list.add(new SelectionModel("", context.getString(R.string.bar)));
        list.add(new SelectionModel("", context.getString(R.string.club)));
        list.add(new SelectionModel("", context.getString(R.string.coffeeShop)));
        return list;
    }
}
