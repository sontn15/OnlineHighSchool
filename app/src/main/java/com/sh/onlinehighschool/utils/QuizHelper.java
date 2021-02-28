package com.sh.onlinehighschool.utils;

import com.sh.onlinehighschool.model.Choice;
import com.sh.onlinehighschool.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class QuizHelper {

    public static int getAnsTrue(ArrayList<Question> questions){
        int ansTrue = 0;
        for (int i = 0; i < questions.size(); i++){
            Question question = questions.get(i);
            if (question.getChoice() != null &&
                    question.getChoice().equals(question.getResult().toUpperCase())){
                ansTrue ++;
            }
        }
        return ansTrue;
    }

    public static double getScore(ArrayList<Question> questions){
        double scorePerQues = (double)10/ (double) questions.size();
        return getAnsTrue(questions) * scorePerQues;
    }

    public static String getStringScore(double score) {
        if (score == 0.00)
            return "0";
        else if (score % 10.00 == 0)
            return String.format(Locale.getDefault(), "%.0f", score);
        else {
            String s = String.format(Locale.getDefault(), "%.2f", score);
            if (s.endsWith("0"))
                return String.format(Locale.getDefault(), "%.1f", score);
            else
                return s;
        }
    }

    public static String unixTimeToDate(long unixTime){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        date.setTime(unixTime);
        return dateFormat.format(date);
    }

    public static String choice(int position){
        if (position == 0) return "A";
        else if (position == 1) return "B";
        else if (position == 2) return "C";
        else if (position == 3) return "D";
        return null;
    }

    public static int optionPosition(String choice){
        if (choice != null){
            if (choice.equals("A")) return 0;
            if (choice.equals("B")) return 1;
            if (choice.equals("C")) return 2;
            if (choice.equals("D")) return 3;
        }
        return -1;
    }

    public static String getTimeCountdown(long unixTime) {
        int hours = (int) ((unixTime / 1000) / 60 / 60);
        long unixOneHour = unixTime - (hours * 60 * 60 * 1000);
        int minutes = hours * 60 + (int) ((unixOneHour / 1000) / 60);
        int seconds = (int) ((unixOneHour / 1000) % 60);
        if (minutes < 100)
            return String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds);
        else {
            return String.format(Locale.getDefault(), "%03d : %02d", minutes, seconds);
        }
    }

    public static String getTimeResult(long unixTime){
        int hours = (int) ((unixTime / 1000) / 60 / 60);
        long unixOneHour = unixTime - (hours * 60 * 60 * 1000);
        int minutes = (int) ((unixOneHour / 1000) / 60);
        int seconds = (int) ((unixOneHour / 1000) % 60);
        double time;
        if (hours > 1){
            return String.format(Locale.getDefault(), "%d giờ, %d phút, %d giây", hours, minutes, seconds);
        } else {
            if (minutes < 1){
                if (unixTime < 1000){
                    time = (double) unixTime/1000;
                    return String.format(Locale.getDefault(), "%.1f giây", time);
                } else {
                    time = seconds + (double) (unixTime - seconds * 1000)/1000;
                    return String.format(Locale.getDefault(), "%.1f giây", time);
                }
            } else {
                return String.format(Locale.getDefault(), "%d phút, %d giây", minutes, seconds);
            }
        }
    }

    public static String setChoice(ArrayList<Question> questions){
        ArrayList<Choice> choices = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++){
            Question question = questions.get(i);
            choices.add(new Choice(
                    question.getId(),
                    question.getResult(),
                    question.getChoice()
            ));
        }
        return choices.toString();
    }

    /**
     * Cập nhật trả lời của người dùng vào câu hỏi
     * @param s ~ history.getChoice
     * @param questions ~ câu hỏi của đề thi
     */
    public static ArrayList<Question> historyQuestions(String s, ArrayList<Question> questions){
        ArrayList<Question> historyQuestions = new ArrayList<>();
        ArrayList<Choice> choices = choicesFromString(s);
        for (int i = 0; i < choices.size(); i++){
            Choice choice = choices.get(i);
            for (int j = 0; j < questions.size(); j++){
                Question question = questions.get(j);
                if (question.getId() == choice.getId()){
                    question.setChoice(choice.getChoice());
                    historyQuestions.add(question);
                }
            }
        }
        return historyQuestions;
    }

    private static ArrayList<Choice> choicesFromString(String s){
        ArrayList<Choice> choices = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Choice choice = new Choice();
                choice.setId(object.getInt("id"));
                choice.setResult(object.getString("result"));
                String userChoice = object.getString("choice");
                if (userChoice.equals("null")){
                    userChoice = null;
                }
                choice.setChoice(userChoice);
                choices.add(choice);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return choices;
    }
}
