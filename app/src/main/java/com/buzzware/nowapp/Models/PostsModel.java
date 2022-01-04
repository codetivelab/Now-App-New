package com.buzzware.nowapp.Models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.buzzware.nowapp.Sessions.UserSessions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostsModel implements Parcelable {
    public NormalUserModel user;
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
    public List<String> likes;

    public PostsModel() {

    }

    public PostsModel(String userID,
                      String createdAt,
                      String tagBusinessId,
                      String tagBusinessName,
                      String tagUserID,
                      String tagUserName,
                      String userPostComment,
                      String userPostThumbnail,
                      String userRating,
                      String userVideoUrl,
                      String userImage,
                      String userName,
                      Boolean pinned,
                      String postId) {
        UserID = userID;
        this.createdAt = createdAt;
        this.tagBusinessId = tagBusinessId;
        this.tagBusinessName = tagBusinessName;
        this.tagUserID = tagUserID;
        this.tagUserName = tagUserName;
        this.userPostComment = userPostComment;
        this.userPostThumbnail = userPostThumbnail;
        this.userRating = userRating;
        this.userVideoUrl = userVideoUrl;
        this.userImage = userImage;
        this.userName = userName;
        this.pinned = pinned;
        this.postId = postId;
    }

    protected PostsModel(Parcel in) {
        UserID = in.readString();
        createdAt = in.readString();
        tagBusinessId = in.readString();
        tagBusinessName = in.readString();
        tagUserID = in.readString();
        tagUserName = in.readString();
        userPostComment = in.readString();
        userPostThumbnail = in.readString();
        userRating = in.readString();
        userVideoUrl = in.readString();
        userImage = in.readString();
        userName = in.readString();
        byte tmpPinned = in.readByte();
        pinned = tmpPinned == 0 ? null : tmpPinned == 1;
        postId = in.readString();
    }

    public static final Creator<PostsModel> CREATOR = new Creator<PostsModel>() {
        @Override
        public PostsModel createFromParcel(Parcel in) {
            return new PostsModel(in);
        }

        @Override
        public PostsModel[] newArray(int size) {
            return new PostsModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(UserID);
        parcel.writeString(createdAt);
        parcel.writeString(tagBusinessId);
        parcel.writeString(tagBusinessName);
        parcel.writeString(tagUserID);
        parcel.writeString(tagUserName);
        parcel.writeString(userPostComment);
        parcel.writeString(userPostThumbnail);
        parcel.writeString(userRating);
        parcel.writeString(userVideoUrl);
        parcel.writeString(userImage);
        parcel.writeString(userName);
        parcel.writeByte((byte) (pinned == null ? 0 : pinned ? 1 : 2));
        parcel.writeString(postId);
    }
}
