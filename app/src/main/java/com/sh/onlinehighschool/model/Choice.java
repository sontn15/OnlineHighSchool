package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Choice implements Parcelable {

    private int id;
    private String result;
    private String choice;

    public Choice() {
    }

    public Choice(int id, String result, String choice) {
        this.id = id;
        this.result = result;
        this.choice = choice;
    }

    private Choice(Parcel in) {
        id = in.readInt();
        result = in.readString();
        choice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(result);
        dest.writeString(choice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Choice> CREATOR = new Creator<Choice>() {
        @Override
        public Choice createFromParcel(Parcel in) {
            return new Choice(in);
        }

        @Override
        public Choice[] newArray(int size) {
            return new Choice[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\" :" + id +
                ", \"result\" : \"" + result + "\"" +
                ", \"choice\" : \"" + choice  + "\"" +
                '}';
    }
}
