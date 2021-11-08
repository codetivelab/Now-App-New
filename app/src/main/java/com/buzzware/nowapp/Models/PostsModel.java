package com.buzzware.nowapp.Models;

import android.content.Context;

import com.buzzware.nowapp.Sessions.UserSessions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostsModel {
    String UserID;
    String createdAt;
    String tagBusinessId;
    String tagBusinessName;
    String tagUserID;
    String tagUserName;
    String userPostComment;
    String userPostThumbnail;
    String userRating;
    String userVideoUrl;
    String userImage;
    String userName;
    Boolean pinned;

    public String getUserImage() {
        return userImage;
    }

    public Boolean hasImage() {

        if (userImage != null && !userImage.isEmpty())
        {
            return true;
        }

        return false;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {

        if (userName == null)

            return "";

        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getPinned() {

        if (pinned == null) {

            return false;
        }

        return pinned;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public String postId;


    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTagBusinessId() {

        if (tagBusinessId == null) {
            return "";
        }

        return tagBusinessId;
    }

    public void setTagBusinessId(String tagBusinessId) {
        this.tagBusinessId = tagBusinessId;
    }

    public String getTagBusinessName() {
        return tagBusinessName;
    }

    public void setTagBusinessName(String tagBusinessName) {
        this.tagBusinessName = tagBusinessName;
    }

    public String getTagUserID() {
        return tagUserID;
    }

    public void setTagUserID(String tagUserID) {
        this.tagUserID = tagUserID;
    }

    public String getTagUserName() {
        return tagUserName;
    }

    public void setTagUserName(String tagUserName) {
        this.tagUserName = tagUserName;
    }

    public String getUserPostComment() {

        if (userPostComment == null)
        {
            return "";
        }

        return userPostComment;
    }

    public void setUserPostComment(String userPostComment) {
        this.userPostComment = userPostComment;
    }

    public String getUserPostThumbnail() {

        if (userPostThumbnail == null)
        {
            return "";
        }

        return userPostThumbnail;
    }

    public void setUserPostThumbnail(String userPostThumbnail) {
        this.userPostThumbnail = userPostThumbnail;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getUserVideoUrl() {
        return userVideoUrl;
    }

    public void setUserVideoUrl(String userVideoUrl) {
        this.userVideoUrl = userVideoUrl;
    }

    public boolean belongsToCurrentUser(Context context, String businessId) {

        if (getUserID().equals(UserSessions.GetUserSession().getFirebaseUserID(context)))
        {
            return true;
        }

        return false;
    }

    public boolean createdToday() {

        if (getDateFromTimeStamp().equalsIgnoreCase(getCurrentDate()))
        {
            return true;
        }

        return false;
    }

    public String getDateFromTimeStamp()
    {
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(Long.parseLong(createdAt));
        String datetime = myFormat.format(date);

        return datetime;
    }

    public String getCurrentDate()
    {
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String datetime = myFormat.format(date);

        return datetime;
    }

    public boolean taggedToCurrentBusiness(Context c, String businessId) {

        if (businessId != null && !businessId.isEmpty() && getTagBusinessId().equals(businessId) && !getTagBusinessId().isEmpty()) {
            return true;
        }

        return false;
    }
}
