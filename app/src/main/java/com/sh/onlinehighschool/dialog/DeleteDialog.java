package com.sh.onlinehighschool.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.callback.OnDeleteListener;
import com.sh.onlinehighschool.model.Exam;


public class DeleteDialog extends BaseDialog implements View.OnClickListener {

    public static DeleteDialog newInstance(Exam exam) {
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putParcelable("EXAM", exam);
        dialog.setArguments(args);
        return dialog;
    }

    private Exam exam;

    @Override
    protected void getData() {
        if (getArguments() != null) {
            exam = getArguments().getParcelable("EXAM");
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.dialog_caution;
    }

    @Override
    protected double dialogWidth() {
        return 0.8;
    }

    @Override
    protected double dialogHeight() {
        return 0;
    }

    @Override
    protected int dialogGravity() {
        return Gravity.CENTER;
    }

    private TextView tvTitle;
    private TextView tvMessage;

    @Override
    protected void initWidgets(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvMessage = view.findViewById(R.id.tv_message);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_confirm).setOnClickListener(this);
    }

    @Override
    protected void configView() {
        tvTitle.setText("Xóa đề thi");
        tvMessage.setText("Bạn có muốn xóa đề thi này không");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_confirm)
            mListener.onDelete(exam);
        dismiss();
    }

    private OnDeleteListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnDeleteListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
}
