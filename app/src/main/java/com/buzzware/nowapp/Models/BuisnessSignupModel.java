package com.buzzware.nowapp.Models;

import android.net.Uri;

import org.json.JSONObject;

import java.util.List;

public class BuisnessSignupModel {
    String buisnessName, buisnessType, buisessEmail, buisnessNumber, applicantFirstName, applicantLastName, applicantNumber, password,
    buisnessLocation, buisnessLatitude, buisnessLongitude, buisnessState, buisnessCity, buisnessZipcode, buisnessResponse, venueAddress;
    Uri buisnessLicenceImage, buisnessLogo, buisnessBackgroundImages;

    public BuisnessSignupModel(){}

    public String getBuisnessResponse() {
        return buisnessResponse;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setBuisnessResponse(String buisnessResponse) {
        this.buisnessResponse = buisnessResponse;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public Uri getBuisnessLogo() {
        return buisnessLogo;
    }

    public Uri getBuisnessBackgroundImages() {
        return buisnessBackgroundImages;
    }

    public Uri getBuisnessLicenceImage() {
        return buisnessLicenceImage;
    }

    public void setBuisnessLicenceImage(Uri buisnessLicenceImage) {
        this.buisnessLicenceImage = buisnessLicenceImage;
    }

    public void setBuisnessLogo(Uri buisnessLogo) {
        this.buisnessLogo = buisnessLogo;
    }

    public void setBuisnessBackgroundImages(Uri buisnessBackgroundImages) {
        this.buisnessBackgroundImages = buisnessBackgroundImages;
    }

    public String getBuisnessName() {
        return buisnessName;
    }

    public void setBuisnessName(String buisnessName) {
        this.buisnessName = buisnessName;
    }

    public String getBuisnessType() {
        return buisnessType;
    }

    public void setBuisnessType(String buisnessType) {
        this.buisnessType = buisnessType;
    }

    public String getBuisessEmail() {
        return buisessEmail;
    }

    public void setBuisessEmail(String buisessEmail) {
        this.buisessEmail = buisessEmail;
    }

    public String getBuisnessNumber() {
        return buisnessNumber;
    }

    public void setBuisnessNumber(String buisnessNumber) {
        this.buisnessNumber = buisnessNumber;
    }

    public String getApplicantFirstName() {
        return applicantFirstName;
    }

    public void setApplicantFirstName(String applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public String getApplicantLastName() {
        return applicantLastName;
    }

    public void setApplicantLastName(String applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public String getApplicantNumber() {
        return applicantNumber;
    }

    public void setApplicantNumber(String applicantNumber) {
        this.applicantNumber = applicantNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBuisnessLocation() {
        return buisnessLocation;
    }

    public void setBuisnessLocation(String buisnessLocation) {
        this.buisnessLocation = buisnessLocation;
    }

    public String getBuisnessLatitude() {
        return buisnessLatitude;
    }

    public void setBuisnessLatitude(String buisnessLatitude) {
        this.buisnessLatitude = buisnessLatitude;
    }

    public String getBuisnessLongitude() {
        return buisnessLongitude;
    }

    public void setBuisnessLongitude(String buisnessLongitude) {
        this.buisnessLongitude = buisnessLongitude;
    }

    public String getBuisnessState() {
        return buisnessState;
    }

    public void setBuisnessState(String buisnessState) {
        this.buisnessState = buisnessState;
    }

    public String getBuisnessCity() {
        return buisnessCity;
    }

    public void setBuisnessCity(String buisnessCity) {
        this.buisnessCity = buisnessCity;
    }

    public String getBuisnessZipcode() {
        return buisnessZipcode;
    }

    public void setBuisnessZipcode(String buisnessZipcode) {
        this.buisnessZipcode = buisnessZipcode;
    }
}
