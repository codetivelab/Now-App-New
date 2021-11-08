package com.buzzware.nowapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessModel implements Parcelable {

    public String businessAddress;
    public String businessBackgroundImage;
    public String businessCity;
    public String businessLatitude;
    public String businessLicenceImage;
    public String businessLogo;
    public String businessLongitude;
    public String businessName;
    public String businessNumber;
    public String businessResponse;
    public String businessTotalRating;
    public String businessType;
//    public String businessVenueAddress;
    public String businessZipcode;

    public BusinessModel()
    {

    }

    public BusinessModel(String businessAddress, String businessBackgroundImage, String businessCity, String businessLatitude, String businessLicenceImage, String businessLogo, String businessLongitude, String businessName, String businessNumber, String businessResponse, String businessTotalRating, String businessType,String businessZipcode) {
        this.businessAddress = businessAddress;
        this.businessBackgroundImage = businessBackgroundImage;
        this.businessCity = businessCity;
        this.businessLatitude = businessLatitude;
        this.businessLicenceImage = businessLicenceImage;
        this.businessLogo = businessLogo;
        this.businessLongitude = businessLongitude;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessResponse = businessResponse;
        this.businessTotalRating = businessTotalRating;
        this.businessType = businessType;
//        this.businessVenueAddress = businessVenueAddress;
        this.businessZipcode = businessZipcode;
    }

    public String getBusinessAddress() {

        if (businessAddress == null) {

            return "";
        }
        return businessAddress;
    }

    public String getBusinessBackgroundImage() {
        return businessBackgroundImage;
    }

    public String getBusinessCity() {

        if (businessCity == null) {

            return "";
        }

        return businessCity;
    }

    public String getBusinessLatitude() {
        return businessLatitude;
    }

    public String getBusinessLicenceImage() {
        return businessLicenceImage;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public String getBusinessLongitude() {
        return businessLongitude;
    }

    public String getBusinessName() {

        if (businessName == null)
            return "";
        return businessName;
    }

    public String getBusinessNumber() {

        if (businessNumber == null)
            return "";

        return businessNumber;
    }

    public String getBusinessResponse() {

        if (businessResponse == null)
            return "";

        return businessResponse;
    }

    public String getBusinessTotalRating() {
        return businessTotalRating;
    }

    public String getBusinessType() {

        if (businessType == null)
            return "";
        return businessType;
    }

//    public String getBusinessVenueAddress() {
//
//        if (businessAddress == null)
//        {
//            return "";
//        }
//
//        return businessVenueAddress;
//    }

    public String getBusinessZipcode() {
        return businessZipcode;
    }

    public static Creator<BusinessModel> getCREATOR() {
        return CREATOR;
    }

    protected BusinessModel(Parcel in) {
        businessAddress = in.readString();
        businessBackgroundImage = in.readString();
        businessCity = in.readString();
        businessLatitude = in.readString();
        businessLicenceImage = in.readString();
        businessLogo = in.readString();
        businessLongitude = in.readString();
        businessName = in.readString();
        businessNumber = in.readString();
        businessResponse = in.readString();
        businessTotalRating = in.readString();
        businessType = in.readString();
//        businessVenueAddress = in.readString();
        businessZipcode = in.readString();
    }

    public static final Creator<BusinessModel> CREATOR = new Creator<BusinessModel>() {
        @Override
        public BusinessModel createFromParcel(Parcel in) {
            return new BusinessModel(in);
        }

        @Override
        public BusinessModel[] newArray(int size) {
            return new BusinessModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessAddress);
        dest.writeString(businessBackgroundImage);
        dest.writeString(businessCity);
        dest.writeString(businessLatitude);
        dest.writeString(businessLicenceImage);
        dest.writeString(businessLogo);
        dest.writeString(businessLongitude);
        dest.writeString(businessName);
        dest.writeString(businessNumber);
        dest.writeString(businessResponse);
        dest.writeString(businessTotalRating);
        dest.writeString(businessType);
//        dest.writeString(businessVenueAddress);
        dest.writeString(businessZipcode);
    }
}



