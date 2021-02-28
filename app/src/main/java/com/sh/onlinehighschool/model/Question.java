package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Question implements Parcelable {

    private int id;
    private String ask;
    private String img;
    private List<String> options;
    private String result;
    private String detail;
    private String choice;

    public Question() {
    }

    public Question(int id, String ask, String img, List<String> options, String result, String detail) {
        this.id = id;
        this.ask = ask;
        this.img = img;
        this.options = options;
        this.result = result;
        this.detail = detail;
    }

    private Question(Parcel in) {
        id = in.readInt();
        ask = in.readString();
        img = in.readString();
        options = in.createStringArrayList();
        result = in.readString();
        detail = in.readString();
        choice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(ask);
        dest.writeString(img);
        dest.writeStringList(options);
        dest.writeString(result);
        dest.writeString(detail);
        dest.writeString(choice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
