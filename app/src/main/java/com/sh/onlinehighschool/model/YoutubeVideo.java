package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class YoutubeVideo implements Serializable, Parcelable {
    private String title;
    private int id;
    private int khoi;
    private String subject;
    private String userId;
    private String videoId;
    private String imageUrl;

    public YoutubeVideo() {
    }

    protected YoutubeVideo(Parcel in) {
        title = in.readString();
        id = in.readInt();
        khoi = in.readInt();
        subject = in.readString();
        userId = in.readString();
        videoId = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<YoutubeVideo> CREATOR = new Creator<YoutubeVideo>() {
        @Override
        public YoutubeVideo createFromParcel(Parcel in) {
            return new YoutubeVideo(in);
        }

        @Override
        public YoutubeVideo[] newArray(int size) {
            return new YoutubeVideo[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getKhoi() {
        return khoi;
    }

    public void setKhoi(int khoi) {
        this.khoi = khoi;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeInt(khoi);
        dest.writeString(subject);
        dest.writeString(userId);
        dest.writeString(videoId);
        dest.writeString(imageUrl);
    }
}
