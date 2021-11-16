package com.buzzware.nowapp.spyglass;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.buzzware.nowapp.spyglass.mentions.Mentionable;

public class Person implements Mentionable {

    private  String userFirstName;
    private  String userLastName;
    private  String userImageUrl;
    private  String userId;

    public String getUserId() {
        return userId;
    }

    public int startingIndex;
    public int endingIndex;

    public Person() {

    }

    public Person(String firstName, String lastName, String pictureURL, String userId) {
        userFirstName = firstName;
        userLastName = lastName;
        userImageUrl = pictureURL;
        this.userId = userId;
    }

    public String getFirstName() {
        return userFirstName;
    }

    public String getLastName() {
        return userLastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getPictureURL() {
        return userImageUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // --------------------------------------------------
    // Mentionable/Suggestible Implementation
    // --------------------------------------------------

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return getFullName();
            case PARTIAL:
                String[] words = getFullName().split(" ");
                return (words.length > 1) ? words[0] : "";
            case NONE:
            default:
                return "";
        }
    }

    @NonNull
    @Override
    public MentionDeleteStyle getDeleteStyle() {
        // People support partial deletion
        // i.e. "John Doe" -> DEL -> "John" -> DEL -> ""
        return MentionDeleteStyle.FULL_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getFullName().hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return getFullName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userFirstName);
        dest.writeString(userLastName);
        dest.writeString(userImageUrl);
        dest.writeString(userId);
    }

    public Person(Parcel in) {
        userFirstName = in.readString();
        userLastName = in.readString();
        userImageUrl = in.readString();
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<Person> CREATOR
            = new Parcelable.Creator<Person>() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    // --------------------------------------------------
    // PersonLoader Class (loads people from JSON file)
    // --------------------------------------------------

}
