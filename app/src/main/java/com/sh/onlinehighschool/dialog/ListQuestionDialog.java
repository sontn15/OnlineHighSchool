package com.sh.onlinehighschool.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.NumberQuestionAdapter;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.callback.OnQuizListener;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.views.AutoRecyclerView;

import java.util.ArrayList;


public class ListQuestionDialog extends BaseDialog {

    public static ListQuestionDialog newInstance(int status, ArrayList<Question> questions) {
        ListQuestionDialog dialog = new ListQuestionDialog();
        Bundle args = new Bundle();
        args.putInt("STATUS", status);
        args.putParcelableArrayList("QUESTIONS", questions);
        dialog.setArguments(args);
        return dialog;
    }

    private int status;
    private ArrayList<Question> questions;

    @Override
    protected void getData() {
        if (getArguments() != null) {
            status = getArguments().getInt("STATUS");
            questions = getArguments().getParcelableArrayList("QUESTIONS");
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.dialog_list_question;
    }

    @Override
    protected double dialogWidth() {
        return 1;
    }

    @Override
    protected double dialogHeight() {
        return 0;
    }

    @Override
    protected int dialogGravity() {
        return Gravity.BOTTOM;
    }

    private LinearLayout layoutDialog;
    private AutoRecyclerView recyclerView;

    @Override
    protected void initWidgets(View view) {
        layoutDialog = view.findViewById(R.id.layout_dialog);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void configView() {
        NumberQuestionAdapter adapter = new NumberQuestionAdapter(status, questions,
                new OnRecyclerViewListener() {
                    @Override
                    public void onItemChange(View view, int position) {
                        mListener.onQuestionChange(position);
                        dismiss();
                    }
                });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        layoutDialog.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up));
    }

    private OnQuizListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnQuizListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
}
