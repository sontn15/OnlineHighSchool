package com.sh.onlinehighschool.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.SubjectRowAdapter;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.callback.OnSubjectListener;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.Pref;

import java.util.ArrayList;


public class SubjectFirebaseDialog extends BaseDialog {

    private DBAssetHelper dbAssetHelper;

    @Override
    protected void getData() {
        dbAssetHelper = new DBAssetHelper(getActivity());
    }

    @Override
    protected int layoutRes() {
        return R.layout.dialog_subject;
    }

    @Override
    protected double dialogWidth() {
        return 1;
    }

    @Override
    protected double dialogHeight() {
        return 1;
    }

    @Override
    protected int dialogGravity() {
        return Gravity.BOTTOM;
    }

    private RelativeLayout layoutDialog;
    private RecyclerView recyclerView;

    @Override
    protected void initWidgets(View view) {
        layoutDialog = view.findViewById(R.id.layout_dialog);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void configView() {
        layoutDialog.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up));
        final ArrayList<Subject> subjects = dbAssetHelper.subjects(Pref.DEFAULT_FACULTY, Pref.DEFAULT_YEAR);
        SubjectRowAdapter adapter = new SubjectRowAdapter(subjects, new OnRecyclerViewListener() {
            @Override
            public void onItemChange(View view, int position) {
                listener.onSubjectChange(subjects.get(position));
                dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private OnSubjectListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnSubjectListener) getTargetFragment();
    }
}
