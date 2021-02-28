package com.sh.onlinehighschool.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.callback.OnCautionDialogListener;


public class CautionDialog extends BaseDialog implements View.OnClickListener {

    public static CautionDialog newInstance(String tag, String tile, String message){
        CautionDialog dialog = new CautionDialog();
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
        tvTitle.setText(title);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_confirm)
            mListener.onConfirm(tag);
        dismiss();
    }

    private OnCautionDialogListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCautionDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }
}
