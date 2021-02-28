package com.sh.onlinehighschool.model;

public class Score {

    private int subjectID;
    private String subjectName;
    private int views;
    private double avgScore;
    private double minScore;
    private double maxScore;

    public Score(int subjectID, String subjectName, int views, double avgScore, double minScore, double maxScore) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.views = views;
        this.avgScore = avgScore;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }
}
