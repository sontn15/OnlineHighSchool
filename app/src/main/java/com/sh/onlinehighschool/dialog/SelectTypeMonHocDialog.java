package com.sh.onlinehighschool.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.callback.OnSelectTypeMonHocDialogListener;


public class SelectTypeMonHocDialog extends BaseDialog implements View.OnClickListener {

    public static SelectTypeMonHocDialog newInstance(String tag, String tile, String message) {
        SelectTypeMonHocDialog dialog = new SelectTypeMonHocDialog();
        Bundle args = new Bundle();
        args.putString("TAG", tag);
        args.putString("TITLE", tile);
        args.putString("MESSAGE", message);
        dialog.setArguments(args);
        return dialog;
    }

    private String tag;
    private String title;
    private String message;

    @Override
    protected void getData() {
        if (getArguments() != null) {
            tag = getArguments().getString("TAG");
            title = getArguments().getString("TITLE");
            message = getArguments().getString("MESSAGE");
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.dialog_select_type_subject;
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
        view.findViewById(R.id.tv_video_monhoc).setOnClickListener(this);
        view.findViewById(R.id.tv_ontap_monhoc).setOnClickListener(this);
    }

    @Override
    protected void configView() {
        tvTitle.setText(title);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_video_monhoc) {
            mListener.onClickVideoMonHoc();
        } else if (view.getId() == R.id.tv_ontap_monhoc) {
            mListener.onClickOnTapMonHoc();
        }
        dismiss();
    }

    private OnSelectTypeMonHocDialogListener mListener;

    public OnSelectTypeMonHocDialogListener getmListener() {
        return mListener;
    }

    public void setmListener(OnSelectTypeMonHocDialogListener mListener) {
        this.mListener = mListener;
    }
}
