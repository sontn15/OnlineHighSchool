package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Comment implements Serializable, Parcelable {

    private YoutubeVideo youtubeVideo;
    private User user;
    private String content;
    private String createdDate;

    public Comment() {
    }

    public YoutubeVideo getYoutubeVideo() {
        return youtubeVideo;
    }

    public void setYoutubeVideo(YoutubeVideo youtubeVideo) {
        this.youtubeVideo = youtubeVideo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    protected Comment(Parcel in) {
        content = in.readString();
        createdDate = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(createdDate);
    }
}
