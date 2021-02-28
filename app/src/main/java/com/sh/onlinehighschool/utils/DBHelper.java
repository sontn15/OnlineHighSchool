package com.sh.onlinehighschool.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sh.onlinehighschool.model.History;
import com.sh.onlinehighschool.model.Score;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_user";
    private static final int DATABASE_VERSION = 10;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createHistoryTbl(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS histories");
        onCreate(db);
    }

    /*Tạo bảng lịch sử thi (offline)*/
    private void createHistoryTbl(SQLiteDatabase db){
        String scrip = "CREATE TABLE histories("
                + "id" + " INTEGER PRIMARY KEY,"
                + "type" + " TEXT,"
                + "name" + " TEXT,"
                + "subject" + " INTEGER,"
                + "exam" + " BIGINT,"
                + "submitted" + " BIGINT,"
                + "time" + " BIGINT,"
                + "score" + " DOUBLE,"
                + "choice" + " TEXT" + ")";
        db.execSQL(scrip);
    }

    /*Tải lần thi gần nhất của đề thi*/
    public History lastHistory(long examID){
        SQLiteDatabase db = this.getReadableDatabase();
        History history = new History();
        String selection = "exam=?";
        String[] args = new String[]{String.valueOf(examID)};
        Cursor c = db.query("histories", null, selection, args,
                null, null, "submitted DESC", "0, 1");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            history.setId(c.getInt(0));
            history.setType(c.getString(1));
            history.setName(c.getString(2));
            history.setSubjectID(c.getInt(3));
            history.setExamID(c.getLong(4));
            history.setSubmitted(c.getLong(5));
            history.setTimePlay(c.getLong(6));
            history.setScore(c.getDouble(7));
            history.setChoice(c.getString(8));
            c.moveToNext();
        }
        c.close();
        return history;
    }

    /*Tải lần thi cao điểm nhất*/
    public History highScoreHistory(long examID){
        SQLiteDatabase db = this.getReadableDatabase();
        History history = new History();
        String selection = "exam=?";
        String[] args = new String[]{String.valueOf(examID)};
        Cursor c = db.query("histories", null, selection, args,
                null, null, "score DESC, time DESC", "0, 1");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            history.setId(c.getInt(0));
            history.setType(c.getString(1));
            history.setName(c.getString(2));
            history.setSubjectID(c.getInt(3));
            history.setExamID(c.getLong(4));
            history.setSubmitted(c.getLong(5));
            history.setTimePlay(c.getLong(6));
            history.setScore(c.getDouble(7));
            history.setChoice(c.getString(8));
            c.moveToNext();
        }
        c.close();
        return history;
    }

    /*Thêm lịch sử thi*/
    public void insertHistory(History history){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", history.getType());
        values.put("name", history.getName());
        values.put("subject", history.getSubjectID());
        values.put("exam", history.getExamID());
        values.put("submitted", history.getSubmitted());
        values.put("time", history.getTimePlay());
        values.put("score", history.getScore());
        values.put("choice", history.getChoice());
        db.insert("histories", null, values);
        db.close();
    }

    /*Tải lịch sử thi*/
    //Tải 50 lần thi gần nhất
    public ArrayList<History> histories(int subjectID){
        ArrayList<History> histories = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = null;
        if (subjectID != 0){
            //Nếu subjectID = 0 -> Tải lịch sử tất cả môn học
            selection = "subject=?";
        }
        String[] args = null;
        if (subjectID != 0){
            args = new String[]{String.valueOf(subjectID)};
        }
        Cursor c = db.query("histories", null, selection, args,
                null, null, "submitted DESC", "0, 50");
        c.moveToFirst();
        while (!c.isAfterLast()){
            History history = new History();
            history.setId(c.getInt(0));
            history.setType(c.getString(1));
            history.setName(c.getString(2));
            history.setSubjectID(c.getInt(3));
            history.setExamID(c.getLong(4));
            history.setSubmitted(c.getLong(5));
            history.setTimePlay(c.getLong(6));
            history.setScore(c.getDouble(7));
            history.setChoice(c.getString(8));
            histories.add(history);
            c.moveToNext();
        }
        c.close();
        return histories;
    }

    /*Tải bảng điểm*/
    public ArrayList<Score> scores(Context context){
        DBAssetHelper dbAssetHelper = new DBAssetHelper(context);
        ArrayList<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT subject, COUNT(id), AVG(score), MIN(score), MAX(score) " +
                "FROM histories GROUP BY subject";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Score score = new Score(
                    c.getInt(0),
                    dbAssetHelper.subject(c.getInt(0)).getName(),
                    c.getInt(1),
                    c.getDouble(2),
                    c.getDouble(3),
                    c.getDouble(4)
            );
            scores.add(score);
            c.moveToNext();
        }
        c.close();
        return scores;
    }
}
