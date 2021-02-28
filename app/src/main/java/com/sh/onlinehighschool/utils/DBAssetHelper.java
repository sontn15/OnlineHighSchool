package com.sh.onlinehighschool.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.model.Subject;

import java.util.ArrayList;
import java.util.List;


public class DBAssetHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "practice.db";
    private static final int DATABASE_VERSION = 1;

    private DBHelper dbHelper;

    public DBAssetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper = new DBHelper(context);
        setForcedUpgrade();
    }

    /*Danh sách môn học*/
    public ArrayList<Subject> subjects(String faculty, String year){
        ArrayList<Subject> list = new ArrayList<>();

        String condition = "";
        if (!faculty.equals(Pref.DEFAULT_FACULTY)){
            condition = condition + "faculty='" + faculty + "'";
        }

        if (getYear(year) != 0){
            if (!condition.equals("")){
                condition = condition + " AND ";
            }
            condition = condition + "year=" + getYear(year);
        }

        if (!condition.equals("")){
            condition = " WHERE " + condition;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM subjects" + condition, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Subject subject = new Subject(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getInt(3),
                    c.getString(4)
            );
            list.add(subject);
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /*Tải môn học theo ID*/
    public Subject subject(int id){
        Subject subject = new Subject();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(id)};
        Cursor c = db.rawQuery("SELECT * FROM subjects WHERE id=?", args);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            subject = new Subject(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getInt(3),
                    c.getString(4)
            );
            c.moveToNext();
        }
        c.close();
        return subject;
    }

    /*Danh sách đề thi theo môn học*/
    public ArrayList<Exam> exams(int subjectID){
        ArrayList<Exam> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(subjectID)};
        String sql = "SELECT exams.id, exams.time, exams.name, " +
                "COUNT(questions.id) AS count " +
                "FROM exams " +
                "INNER JOIN questions ON questions.exam = exams.id " +
                "WHERE exams.subject=? " +
                "GROUP BY exams.id";
        Cursor c = db.rawQuery(sql, args);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getInt(3) != 0){
                Exam exam = new Exam(
                        c.getLong(0),
                        subjectID,
                        c.getInt(1),
                        c.getInt(3),
                        1,
                        c.getString(2),
                        dbHelper.lastHistory(c.getInt(0)),
                        dbHelper.highScoreHistory(c.getInt(0))
                );
                list.add(exam);
            }
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /*Đề thi theo ID*/
    public Exam exam(long id){
        Exam exam = new Exam();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(id)};
        Cursor c = db.rawQuery("SELECT * FROM exams WHERE id=?", args);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            exam = new Exam(
                    c.getLong(0),
                    c.getInt(1),
                    c.getInt(2),
                    0,
                    1,
                    c.getString(3),
                    null, null
            );
            c.moveToNext();
        }
        c.close();
        return exam;
    }

    /*Câu hỏi theo đề thi*/
    public ArrayList<Question> questions(long examID){
        ArrayList<Question> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(examID)};
        Cursor c = db.rawQuery("SELECT * FROM questions WHERE exam=?", args);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Question question = new Question();
            question.setId(c.getInt(0));
            question.setAsk(c.getString(2));
            question.setImg(c.getString(3));
            List<String> options = new ArrayList<>();
            if (c.getString(4) != null  && !c.getString(4).trim().equals("")){
                options.add(c.getString(4));
            }
            if (c.getString(5) != null  && !c.getString(5).trim().equals("")){
                options.add(c.getString(5));
            }
            if (c.getString(6) != null  && !c.getString(6).trim().equals("")){
                options.add(c.getString(6));
            }
            if (c.getString(7) != null  && !c.getString(7).trim().equals("")){
                options.add(c.getString(7));
            }
            question.setOptions(options);
            question.setResult(c.getString(8));
            question.setDetail(c.getString(9));
            list.add(question);
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /*Danh sách khoa*/
    public ArrayList<String> faculties(){
        ArrayList<String> list = new ArrayList<>();
        list.add(Pref.DEFAULT_FACULTY);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT faculty FROM subjects GROUP BY faculty", null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            list.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /*Danh sách năm*/
    public ArrayList<String> years(){
        ArrayList<String> list = new ArrayList<>();
        list.add(Pref.DEFAULT_YEAR);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT year FROM subjects GROUP BY year ORDER BY year ASC", null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            list.add("Năm thứ " + c.getInt(0));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    private static int getYear(String s){
        int year = 0;
        if (s != null){
            String[] words = s.split("\\s");
            String last = words[words.length - 1];
            try {
                year =  Integer.parseInt(String.valueOf(last));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return year;
    }
}
