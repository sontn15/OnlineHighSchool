package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

public class History implements Parcelable {

    private int id;
    private String type;
    private String name;
    private int subjectID;
    private long examID;
    private long submitted;
    private long timePlay;
    private double score;
    private String choice;

    public History() {
    }

    private History(Parcel in) {
        id = in.readInt();
        type = in.readString();
        name = in.readString();
        subjectID = in.readInt();
        examID = in.readLong();
        submitted = in.readLong();
        timePlay = in.readLong();
        score = in.readDouble();
        choice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(subjectID);
        dest.writeLong(examID);
        dest.writeLong(submitted);
        dest.writeLong(timePlay);
        dest.writeDouble(score);
        dest.writeString(choice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public long getExamID() {
        return examID;
    }

    public void setExamID(long examID) {
        this.examID = examID;
    }

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    public long getTimePlay() {
        return timePlay;
    }

    public void setTimePlay(long timePlay) {
        this.timePlay = timePlay;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
