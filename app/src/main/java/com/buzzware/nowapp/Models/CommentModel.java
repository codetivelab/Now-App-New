package com.buzzware.nowapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.auth.User;

import java.util.List;

public class CommentModel implements Parcelable {

    public String id;
    public Long time;
    public String postId;
    public String commenterId;
    public String text;
    public String imageUrl;
    public List<MentionUser> mentioned;
    public List<String> likes;
    public int repliesCount;
    public List<ReplyModel> replies;

    public NormalUserModel commenter;

    public CommentModel() {

    }

    protected CommentModel(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        postId = in.readString();
        commenterId = in.readString();
        text = in.readString();
        imageUrl = in.readString();
        likes = in.createStringArrayList();
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        parcel.writeString(postId);
        parcel.writeString(commenterId);
        parcel.writeString(text);
        parcel.writeString(imageUrl);
        parcel.writeStringList(likes);
    }
}
