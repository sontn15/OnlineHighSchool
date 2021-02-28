package com.sh.onlinehighschool.callback;


import com.sh.onlinehighschool.model.Exam;

public interface OnFirebaseListener {

    void onLoading(boolean isLoading);

    void showExam(Exam exam);

}
