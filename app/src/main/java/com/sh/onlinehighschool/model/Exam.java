package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Exam implements Parcelable {

    private long id;
    private int subjectID;
    private int time;
    private int ques;  //Số lượng câu hỏi
    private int status;
    private String name;
    private History lastHistory;
    private History highScoreHistory;

    public Exam() {
    }

    public Exam(long id, int subjectID, int time, int ques, int status, String name, History lastHistory, History highScoreHistory) {
        this.id = id;
        this.subjectID = subjectID;
        this.time = time;
        this.ques = ques;
        this.status = status;
        this.name = name;
        this.lastHistory = lastHistory;
        this.highScoreHistory = highScoreHistory;
    }

    protected Exam(Parcel in) {
        id = in.readLong();
        subjectID = in.readInt();
        time = in.readInt();
        ques = in.readInt();
        status = in.readInt();
        name = in.readString();
        lastHistory = in.readParcelable(History.class.getClassLoader());
        highScoreHistory = in.readParcelable(History.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(subjectID);
        dest.writeInt(time);
        dest.writeInt(ques);
        dest.writeInt(status);
        dest.writeString(name);
        dest.writeParcelable(lastHistory, flags);
        dest.writeParcelable(highScoreHistory, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exam> CREATOR = new Creator<Exam>() {
        @Override
        public Exam createFromParcel(Parcel in) {
            return new Exam(in);
        }

        @Override
        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getQues() {
        return ques;
    }

    public void setQues(int ques) {
        this.ques = ques;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public History getLastHistory() {
        return lastHistory;
    }

    public void setLastHistory(History lastHistory) {
        this.lastHistory = lastHistory;
    }

    public History getHighScoreHistory() {
        return highScoreHistory;
    }

    public void setHighScoreHistory(History highScoreHistory) {
        this.highScoreHistory = highScoreHistory;
    }
}
