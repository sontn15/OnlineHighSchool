package com.sh.onlinehighschool.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

    private int id;
    private String name;
    private String faculty;
    private int year;
    private String icon;

    public Subject() {
    }

    public Subject(int id, String name, String faculty, int year, String icon) {
        this.id = id;
        this.name = name;
        this.faculty = faculty;
        this.year = year;
        this.icon = icon;
    }

    private Subject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        faculty = in.readString();
        year = in.readInt();
        icon = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(faculty);
        dest.writeInt(year);
        dest.writeString(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return name;
    }
}
