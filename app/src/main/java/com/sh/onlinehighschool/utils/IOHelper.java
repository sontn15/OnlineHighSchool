package com.sh.onlinehighschool.utils;

import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.model.TimeTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class IOHelper {

    public static ArrayList<Question> questions(String s) {
        ArrayList<Question> questions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray array = jsonObject.getJSONArray("questions");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Question question = new Question();
                question.setId(object.getInt("id"));
                question.setAsk(object.getString("ask"));
                if (object.has("img"))
                    question.setImg(object.getString("img"));
                List<String> options = new ArrayList<>();
                if (object.has("op1")) {
                    options.add(object.getString("op1"));
                }
                if (object.has("op2")) {
                    options.add(object.getString("op2"));
                }
                if (object.has("op3")) {
                    options.add(object.getString("op3"));
                }
                if (object.has("op4")) {
                    options.add(object.getString("op4"));
                }
                question.setOptions(options);
                question.setResult(object.getString("result"));
                if (object.has("detail"))
                    question.setDetail(object.getString("detail"));
                questions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static ArrayList<TimeTable> timeTables(String s) {
        ArrayList<TimeTable> data = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray array = jsonObject.getJSONArray("timetables");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                TimeTable timeTable = new TimeTable();
                timeTable.setId(object.getInt("id"));
                timeTable.setThu(object.getString("thu"));
                if (object.has("tiet1Sang"))
                    timeTable.setTiet1Sang(object.getString("tiet1Sang"));
                if (object.has("tiet2Sang"))
                    timeTable.setTiet2Sang(object.getString("tiet2Sang"));
                if (object.has("tiet3Sang"))
                    timeTable.setTiet3Sang(object.getString("tiet3Sang"));
                if (object.has("tiet4Sang"))
                    timeTable.setTiet4Sang(object.getString("tiet4Sang"));
                if (object.has("tiet5Sang"))
                    timeTable.setTiet5Sang(object.getString("tiet5Sang"));

                if (object.has("tiet1Chieu"))
                    timeTable.setTiet1Chieu(object.getString("tiet1Chieu"));
                if (object.has("tiet2Chieu"))
                    timeTable.setTiet2Chieu(object.getString("tiet2Chieu"));
                if (object.has("tiet3Chieu"))
                    timeTable.setTiet3Chieu(object.getString("tiet3Chieu"));
                if (object.has("tiet4Chieu"))
                    timeTable.setTiet4Chieu(object.getString("tiet4Chieu"));
                if (object.has("tiet5Chieu"))
                    timeTable.setTiet5Chieu(object.getString("tiet5Chieu"));

                if (object.has("hocChieuKhong"))
                    timeTable.setHocChieuKhong(object.getInt("hocChieuKhong"));

                data.add(timeTable);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}
