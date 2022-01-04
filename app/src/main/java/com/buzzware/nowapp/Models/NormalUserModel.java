package com.buzzware.nowapp.Models;

public class NormalUserModel {

    String userEmail;
    public String id;
    String userFirstName;
    String userLastName;
    String userImageUrl;
    String userFollowers = "0";
    String userFollowings = "0";
    public String userStatus;
    String userPassword;
    String userPhoneNumber;
    String userToken;
    String userType;

    public NormalUserModel(String userEmail, String userFirstName, String userLastName, String userImageUrl, String userFollowers, String userFollowings, String userPassword, String userPhoneNumber, String userToken, String userType) {
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userImageUrl = userImageUrl;
        this.userFollowers = userFollowers;
        this.userFollowings = userFollowings;
        this.userPassword = userPassword;
        this.userPhoneNumber = userPhoneNumber;
        this.userToken = userToken;
        this.userType = userType;
    }

    public NormalUserModel() {

    }

    public String getUserFollowers() {

        if (userFollowers == null)
            return "";


        return userFollowers;
    }

    public void setUserFollowers(String userFollowers) {
        this.userFollowers = userFollowers;
    }

    public String getUserFollowings() {

        if (userFollowings == null)
            return "";

        return userFollowings;
    }

    public void setUserFollowings(String userFollowings) {
        this.userFollowings = userFollowings;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFirstName() {


        if (userFirstName == null)
            return "";

        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {

        if (userLastName == null)
            return "";

        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhoneNumber() {
        if (userPhoneNumber == null)
            return "";

        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserType() {

        if (userType == null)
            return "";
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
