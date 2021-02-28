package com.sh.onlinehighschool.utils;

import com.sh.onlinehighschool.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class IOHelper {

    public static ArrayList<Question> questions(String s){
        ArrayList<Question> questions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray array = jsonObject.getJSONArray("questions");
            for (int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                Question question = new Question();
                question.setId(object.getInt("id"));
                question.setAsk(object.getString("ask"));
                if (object.has("img"))
                    question.setImg(object.getString("img"));
                List<String> options = new ArrayList<>();
                if (object.has("op1")){
                    options.add(object.getString("op1"));
                }
                if (object.has("op2")){
                    options.add(object.getString("op2"));
                }
                if (object.has("op3")){
                    options.add(object.getString("op3"));
                }
                if (object.has("op4")){
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

}
