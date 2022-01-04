package com.buzzware.nowapp.Models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantDataModel {

    public Boolean userDeleted = true;
    String id, businessName, businessType, businessNumber, businessAddress, businessCity, businessState, businessZipcode,
            businessLatitude, businessLongitude, businessLogo, businessBackgroundImage, businessLicenceImage, businessVenueAddress,
            businessResponse, businessTotalRating;

    public RestaurantDataModel() {
    }

    public RestaurantDataModel(String id, String businessName, String businessType, String businessNumber, String businessAddress, String businessCity, String businessState, String businessZipcode, String businessLatitude, String businessLongitude, String businessLogo, String businessBackgroundImage, String businessLicenceImage, String businessVenueAddress, String businessResponse, String businessTotalRating) {
        this.id = id;
        this.businessName = businessName;
        this.businessType = businessType;
        this.businessNumber = businessNumber;
        this.businessAddress = businessAddress;
        this.businessCity = businessCity;
        this.businessState = businessState;
        this.businessZipcode = businessZipcode;
        this.businessLatitude = businessLatitude;
        this.businessLongitude = businessLongitude;
        this.businessLogo = businessLogo;
        this.businessBackgroundImage = businessBackgroundImage;
        this.businessLicenceImage = businessLicenceImage;
        this.businessVenueAddress = businessVenueAddress;
        this.businessResponse = businessResponse;
        this.businessTotalRating = businessTotalRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessCity() {
        return businessCity;
    }

    public void setBusinessCity(String businessCity) {
        this.businessCity = businessCity;
    }

    public String getBusinessState() {
        return businessState;
    }

    public void setBusinessState(String businessState) {
        this.businessState = businessState;
    }

    public String getBusinessZipcode() {
        return businessZipcode;
    }

    public void setBusinessZipcode(String businessZipcode) {
        this.businessZipcode = businessZipcode;
    }

    public String getBusinessLatitude() {
        return businessLatitude;
    }

    public void setBusinessLatitude(String businessLatitude) {
        this.businessLatitude = businessLatitude;
    }

    public String getBusinessLongitude() {
        return businessLongitude;
    }

    public void setBusinessLongitude(String businessLongitude) {
        this.businessLongitude = businessLongitude;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }

    public String getBusinessBackgroundImage() {
        return businessBackgroundImage;
    }

    public void setBusinessBackgroundImage(String businessBackgroundImage) {
        this.businessBackgroundImage = businessBackgroundImage;
    }

    public String getBusinessLicenceImage() {
        return businessLicenceImage;
    }

    public void setBusinessLicenceImage(String businessLicenceImage) {
        this.businessLicenceImage = businessLicenceImage;
    }

    public String getBusinessVenueAddress() {
        return businessVenueAddress;
    }

    public void setBusinessVenueAddress(String businessVenueAddress) {
        this.businessVenueAddress = businessVenueAddress;
    }

    public String getBusinessResponse() {
        return businessResponse;
    }

    public void setBusinessResponse(String businessResponse) {
        this.businessResponse = businessResponse;
    }

    public String getBusinessTotalRating() {
        return businessTotalRating;
    }

    public void setBusinessTotalRating(String businessTotalRating) {
        this.businessTotalRating = businessTotalRating;
    }

    public String GetIntensity(int day, int hour) {
        String intensity = "-2";
        int finalIndex;
        try {
            JSONObject jsonObject = new JSONObject(businessResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("analysis");
            JSONObject jsonObjectCurrentDay = jsonArray.getJSONObject(day);
            JSONArray jsonArrayHourAnalysis = jsonObjectCurrentDay.getJSONArray("hour_analysis");
            if (hour >= 6) {
                finalIndex = hour - 6;
            } else {
                finalIndex = hour + 18;
            }
            JSONObject jsonObjectHourData = jsonArrayHourAnalysis.getJSONObject(finalIndex);
            intensity = jsonObjectHourData.getString("intensity_nr");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return intensity;
    }
}
