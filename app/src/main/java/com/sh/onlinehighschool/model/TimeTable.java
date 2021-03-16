package com.sh.onlinehighschool.model;

import java.io.Serializable;

public class TimeTable implements Serializable {
    private int id;

    private String thu;

    private String tiet1Sang;
    private String tiet2Sang;
    private String tiet3Sang;
    private String tiet4Sang;
    private String tiet5Sang;


    private int hocChieuKhong;

    private String tiet1Chieu;
    private String tiet2Chieu;
    private String tiet3Chieu;
    private String tiet4Chieu;
    private String tiet5Chieu;

    public TimeTable() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getTiet1Sang() {
        return tiet1Sang;
    }

    public void setTiet1Sang(String tiet1Sang) {
        this.tiet1Sang = tiet1Sang;
    }

    public String getTiet2Sang() {
        return tiet2Sang;
    }

    public void setTiet2Sang(String tiet2Sang) {
        this.tiet2Sang = tiet2Sang;
    }

    public String getTiet3Sang() {
        return tiet3Sang;
    }

    public void setTiet3Sang(String tiet3Sang) {
        this.tiet3Sang = tiet3Sang;
    }

    public String getTiet4Sang() {
        return tiet4Sang;
    }

    public void setTiet4Sang(String tiet4Sang) {
        this.tiet4Sang = tiet4Sang;
    }

    public String getTiet5Sang() {
        return tiet5Sang;
    }

    public void setTiet5Sang(String tiet5Sang) {
        this.tiet5Sang = tiet5Sang;
    }

    public int getHocChieuKhong() {
        return hocChieuKhong;
    }

    public void setHocChieuKhong(int hocChieuKhong) {
        this.hocChieuKhong = hocChieuKhong;
    }

    public String getTiet1Chieu() {
        return tiet1Chieu;
    }

    public void setTiet1Chieu(String tiet1Chieu) {
        this.tiet1Chieu = tiet1Chieu;
    }

    public String getTiet2Chieu() {
        return tiet2Chieu;
    }

    public void setTiet2Chieu(String tiet2Chieu) {
        this.tiet2Chieu = tiet2Chieu;
    }

    public String getTiet3Chieu() {
        return tiet3Chieu;
    }

    public void setTiet3Chieu(String tiet3Chieu) {
        this.tiet3Chieu = tiet3Chieu;
    }

    public String getTiet4Chieu() {
        return tiet4Chieu;
    }

    public void setTiet4Chieu(String tiet4Chieu) {
        this.tiet4Chieu = tiet4Chieu;
    }

    public String getTiet5Chieu() {
        return tiet5Chieu;
    }

    public void setTiet5Chieu(String tiet5Chieu) {
        this.tiet5Chieu = tiet5Chieu;
    }
}
