package com.sh.onlinehighschool.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.utils.Units;


public abstract class BaseDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.base_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout layoutParent = inflateView.findViewById(R.id.layout_parent);
        layoutParent.setGravity(dialogGravity());
        FrameLayout rootView = inflateView.findViewById(R.id.root);
        if (isCancelable()){
            inflateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = marginHorizontal();
        params.rightMargin = marginHorizontal();
        if (dialogHeight() != 0.0){
            if (dialogGravity() == Gravity.CENTER){
                params.topMargin = marginVertical();
                params.bottomMargin = marginVertical();
            } else {
                params.bottomMargin = 2 * marginVertical() - Units.dpToPx(48);
                params.topMargin = Units.dpToPx(48);
            }
        }
        rootView.setLayoutParams(params);

        View view = View.inflate(getActivity(), layoutRes(), null);
        rootView.addView(view);

        initWidgets(view);
        return inflateView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configView();
    }

    protected abstract void getData();

    protected abstract int layoutRes();

    protected abstract double dialogWidth();

    protected abstract double dialogHeight();

    protected abstract int dialogGravity();

    protected abstract void initWidgets(View view);

    protected abstract void configView();

    private int marginHorizontal() {
        int width = (int) (dialogSize().x * dialogWidth());
        return (dialogSize().x - width) / 2;
    }

    private int marginVertical(){
        int height = (int) (dialogSize().y * dialogHeight());
        return (dialogSize().y - height) / 2;
    }

    protected Point dialogSize() {
        Point size = new Point();
        Display display = getDialog().getWindow().getWindowManager().getDefaultDisplay();
        display.getSize(size);
        return size;
    }
}
